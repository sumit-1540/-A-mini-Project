package com.smartshelf.services;

import com.smartshelf.dto.HandwrittenListParseResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class HandwrittenListProcessorService {

    private final GeminiAiService geminiAiService;

    public HandwrittenListProcessorService(GeminiAiService geminiAiService) {
        this.geminiAiService = geminiAiService;
    }

    public HandwrittenListParseResponse processHandwrittenList(MultipartFile file, String manualTextInput) {
        return geminiAiService.processHandwrittenSlipWithGemini(file, manualTextInput, null, null);
    }

    public HandwrittenListParseResponse processHandwrittenListWithGemini(MultipartFile file, String manualTextInput, String apiKey, String model) {
        return geminiAiService.processHandwrittenSlipWithGemini(file, manualTextInput, apiKey, model);
    }
}
