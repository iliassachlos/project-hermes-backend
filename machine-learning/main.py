import ssl
import asyncio
from fastapi import FastAPI, HTTPException
from py_eureka_client import eureka_client
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from transformers import pipeline

app = FastAPI()
service_port = 8000
max_token_length = 512

# CORS middleware to allow requests from Spring Gateway
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8083"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Register service to Eureka
async def register_with_eureka():
    try:
        await eureka_client.init_async(
            eureka_server="http://localhost:8761/eureka",
            app_name="machine-learning",
            instance_port=service_port
        )
    except Exception as e:
        print(f"Failed to register with Eureka: {e}")


# Disable SSL certificate verification
ssl._create_default_https_context = ssl._create_unverified_context


# Pydantic model for request body
class SentimentRequest(BaseModel):
    text: str


# Load the sentiment analysis model
sentiment_analyzer = pipeline("sentiment-analysis", model="nlptown/bert-base-multilingual-uncased-sentiment")


# Function to split text into chunks of
def split_text_into_chunks(text, max_token_length):
    words = text.split()
    for i in range(0, len(words), max_token_length):
        yield ' '.join(words[i:i + max_token_length])


@app.post("/api/machine-learning/sentiment")
async def analyze_sentiment(article_content: SentimentRequest):
    # Check if text is empty
    if not article_content.text.strip():
        raise HTTPException(status_code=404, detail="Text is empty")

    # Split text into chunks
    chunks = list(split_text_into_chunks(article_content.text, max_token_length))

    total_score = 0
    total_chunks = len(chunks)

    for chunk in chunks:
        # Truncate the chunk if it exceeds the maximum token length
        truncated_chunk = chunk[:max_token_length]

        # Perform sentiment analysis
        analysis = sentiment_analyzer(truncated_chunk)

        # Extract start rating from the sentiment analysis result
        star_rating = int(analysis[0]['label'].split()[0])

        total_score = total_score + star_rating

    # Calculate the average star rating
    average_star_rating = total_score / total_chunks

    return int(average_star_rating)


# Run registration with Eureka asynchronously
asyncio.create_task(register_with_eureka())
