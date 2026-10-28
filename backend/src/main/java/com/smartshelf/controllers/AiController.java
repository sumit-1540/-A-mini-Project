package com.smartshelf.controllers;

import com.smartshelf.dto.*;
import com.smartshelf.services.DemandPredictionService;
import com.smartshelf.services.HandwrittenListProcessorService;
import com.smartshelf.services.RecommendationService;
import com.smartshelf.services.SmartDiscountService;
import com.smartshelf.services.SmartReorderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final RecommendationService recommendationService;
    private final DemandPredictionService demandPredictionService;
    private final SmartReorderService smartReorderService;
    private final SmartDiscountService smartDiscountService;
    private final HandwrittenListProcessorService handwrittenListProcessorService;

    public AiController(RecommendationService recommendationService, DemandPredictionService demandPredictionService, SmartReorderService smartReorderService, SmartDiscountService smartDiscountService, HandwrittenListProcessorService handwrittenListProcessorService) {
        this.recommendationService = recommendationService;
        this.demandPredictionService = demandPredictionService;
        this.smartReorderService = smartReorderService;
        this.smartDiscountService = smartDiscountService;
        this.handwrittenListProcessorService = handwrittenListProcessorService;
    }

    @PostMapping("/recommendations")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(
            @RequestBody(required = false) List<Long> currentCartProductIds,
            @RequestParam(required = false) Long customerId) {
        return ResponseEntity.ok(recommendationService.getRecommendationsForCart(currentCartProductIds, customerId));
    }

    @GetMapping("/demand-forecast")
    public ResponseEntity<List<DemandPredictionDTO>> getDemandForecast() {
        return ResponseEntity.ok(demandPredictionService.getDemandPredictions());
    }

    @GetMapping("/ml-metrics")
    public ResponseEntity<Object> getMlMetrics() {
        return ResponseEntity.ok(demandPredictionService.getMlBenchmarkMetrics());
    }

    @PostMapping("/simulate-demand")
    public ResponseEntity<SimulationResponse> simulateDemand(@RequestBody SimulationRequest request) {
        return ResponseEntity.ok(demandPredictionService.simulateDemand(request));
    }

    @GetMapping("/smart-reorder")
    public ResponseEntity<List<SmartReorderDTO>> getSmartReorders() {
        return ResponseEntity.ok(smartReorderService.getReorderSuggestions());
    }

    @GetMapping("/smart-discounts")
    public ResponseEntity<List<SmartDiscountDTO>> getSmartDiscounts() {
        return ResponseEntity.ok(smartDiscountService.getDiscountRecommendations());
    }

    @PostMapping(value = "/handwritten-list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HandwrittenListParseResponse> parseHandwrittenList(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "apiKey", required = false) String apiKey,
            @RequestParam(value = "model", required = false) String model) {
        return ResponseEntity.ok(handwrittenListProcessorService.processHandwrittenListWithGemini(file, text, apiKey, model));
    }
}
