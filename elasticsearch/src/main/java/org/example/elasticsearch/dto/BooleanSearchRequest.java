package org.example.elasticsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BooleanSearchRequest {
    private List<Map<String, String>> must;
    private List<Map<String, String>> should;
    private List<Map<String, String>> must_not;
}
