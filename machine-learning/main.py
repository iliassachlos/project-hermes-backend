import ssl
import nltk
import asyncio
import py_eureka_client.eureka_client as eureka_client
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from nltk.sentiment import SentimentIntensityAnalyzer
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
service_port = 8000

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8083"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Register service to Eureka
async def register_with_eureka():
    await eureka_client.init_async(
        eureka_server="http://localhost:8761",
        app_name="machine-learning",
        instance_port=service_port
    )


# Disable SSL certificate verification
ssl._create_default_https_context = ssl._create_unverified_context
nltk.download('vader_lexicon')


class SentimentRequest(BaseModel):
    text: str


@app.post("/api/machine-learning/sentiment")
async def analyze_sentiment(request: SentimentRequest):
    # Check if text is empty
    if not request.text.strip():
        raise HTTPException(status_code=404, detail="Text is empty")

    # Initialize SentimentIntensityAnalyzer
    sia = SentimentIntensityAnalyzer()

    # Perform sentiment analysis using NLTK's VADER
    sentiment_scores = sia.polarity_scores(request.text)
    compound_score = sentiment_scores['compound']

    return {"sentiment_score": compound_score}


# Run registration with Eureka asynchronously
asyncio.create_task(register_with_eureka())
