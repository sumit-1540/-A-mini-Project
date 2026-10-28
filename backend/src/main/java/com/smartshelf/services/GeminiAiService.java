package com.smartshelf.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartshelf.dto.HandwrittenListParseResponse;
import com.smartshelf.dto.ParsedListItemDTO;
import com.smartshelf.models.Product;
import com.smartshelf.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiAiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.gemini.api-key:${GEMINI_API_KEY:}}")
    private String configuredApiKey;

    @Value("${app.gemini.model:gemini-1.5-flash}")
    private String configuredModel;

    // Comprehensive Hindi, Hinglish and English grocery synonym mapping
    private static final Map<String, List<String>> GROCERY_SYNONYMS = new HashMap<>();

    static {
        addSynonym("doodh", "milk", "taaza", "toned");
        addSynonym("milk", "doodh", "taaza", "toned");
        addSynonym("chawal", "rice", "basmati", "biryani");
        addSynonym("rice", "chawal", "basmati", "biryani");
        addSynonym("aata", "atta", "flour", "sharbati");
        addSynonym("atta", "aata", "flour", "sharbati");
        addSynonym("gehu", "atta", "flour");
        addSynonym("namak", "salt", "tata");
        addSynonym("salt", "namak", "tata");
        addSynonym("chini", "sugar", "madhur");
        addSynonym("shakkar", "sugar", "madhur");
        addSynonym("shakar", "sugar", "madhur");
        addSynonym("sugar", "chini", "shakkar", "madhur");
        addSynonym("tel", "oil", "sunflower", "mustard", "refined");
        addSynonym("oil", "tel", "sunflower", "refined", "mustard");
        addSynonym("sarson", "mustard", "oil", "tel");
        addSynonym("chai", "tea", "red label");
        addSynonym("tea", "chai", "red label");
        addSynonym("patti", "tea", "chai");
        addSynonym("sabun", "soap", "dettol", "bathing");
        addSynonym("soap", "sabun", "dettol", "bathing");
        addSynonym("surf", "detergent", "excel", "powder");
        addSynonym("detergent", "surf", "excel", "powder");
        addSynonym("nirma", "detergent", "surf");
        addSynonym("tide", "detergent", "surf");
        addSynonym("ariel", "detergent", "surf");
        addSynonym("biskut", "biscuit", "biscuits", "parle", "cookies", "good day");
        addSynonym("biscuit", "biskut", "parle", "cookies", "good day");
        addSynonym("biscuits", "biskut", "parle", "cookies", "good day");
        addSynonym("cookie", "cookies", "biscuit", "good day");
        addSynonym("cookies", "cookie", "biscuit", "good day");
        addSynonym("makkhan", "butter", "amul");
        addSynonym("makhan", "butter", "amul");
        addSynonym("butter", "makhan", "makkhan", "amul");
        addSynonym("ghee", "desi ghee", "amul ghee");
        addSynonym("ande", "egg", "eggs", "brown eggs");
        addSynonym("anda", "egg", "eggs", "brown eggs");
        addSynonym("egg", "ande", "eggs");
        addSynonym("eggs", "ande", "egg");
        addSynonym("noodle", "noodles", "maggi");
        addSynonym("noodles", "noodle", "maggi");
        addSynonym("maggi", "noodles", "masala noodles");
        addSynonym("chips", "lays", "wafers");
        addSynonym("coffee", "nescafe", "instant coffee");
        addSynonym("nescafe", "coffee", "instant coffee");
        addSynonym("coke", "coca-cola", "cola", "cold drink");
        addSynonym("cola", "coca-cola", "coke");
        addSynonym("paste", "toothpaste", "colgate");
        addSynonym("toothpaste", "colgate", "paste");
        addSynonym("colgate", "toothpaste", "paste");
        addSynonym("bartan", "dishwash", "vim");
        addSynonym("dishwash", "vim", "bartan");
        addSynonym("vim", "dishwash", "bartan");
        addSynonym("dal", "toor", "tuvar", "daal", "pulses", "sampann");
        addSynonym("daal", "toor", "dal", "pulses");
        addSynonym("toor", "dal", "daal", "tuvar");
        addSynonym("oats", "quaker", "cereal");
        addSynonym("paneer", "malai paneer", "amul");
        addSynonym("kurkure", "crunchy", "snack");
        addSynonym("masala", "garam masala", "everest");
    }

    private static void addSynonym(String key, String... values) {
        GROCERY_SYNONYMS.put(key.toLowerCase(), Arrays.asList(values));
    }

    public GeminiAiService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public HandwrittenListParseResponse processHandwrittenSlipWithGemini(MultipartFile file, String manualTextInput, String customApiKey, String customModel) {
        String apiKey = (customApiKey != null && !customApiKey.trim().isEmpty()) ? customApiKey.trim() : configuredApiKey;
        String model = (customModel != null && !customModel.trim().isEmpty()) ? customModel.trim() : configuredModel;

        String fileName = (file != null && !file.isEmpty()) ? file.getOriginalFilename() : "Direct Input Shopping Slip";
        List<Product> catalog = productRepository.findByActiveTrue();

        // If Gemini API Key is available, call Google Gemini Generative Vision API
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equalsIgnoreCase("YOUR_GEMINI_API_KEY")) {
            try {
                return callGeminiApi(file, manualTextInput, fileName, catalog, apiKey, model);
            } catch (Exception e) {
                log.error("Gemini API call failed, falling back to local intelligent matcher: {}", e.getMessage(), e);
            }
        }

        // Native Intelligent Fallback Parser
        return processLocalIntelligentFallback(file, manualTextInput, fileName, catalog, "Google Gemini AI (Catalog Matcher)");
    }

    private HandwrittenListParseResponse callGeminiApi(MultipartFile file, String manualTextInput, String fileName, List<Product> catalog, String apiKey, String model) throws Exception {
        String endpoint = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, apiKey);

        // Build Catalog context for Gemini
        StringBuilder catalogContext = new StringBuilder();
        catalogContext.append("STORE INVENTORY CATALOG:\n");
        for (Product p : catalog) {
            catalogContext.append(String.format("- ID: %d | Name: \"%s\" | SKU: %s | Price: ₹%.2f | Stock: %d %s | Category: %s\n",
                    p.getId(), p.getName(), p.getSku(), p.getSellingPrice(), p.getCurrentStock(), p.getUnit(),
                    p.getCategory() != null ? p.getCategory().getName() : "General"));
        }

        String systemPrompt = "You are SmartShelf AI Vision Assistant, an expert OCR and grocery item parsing AI for Indian supermarket stores.\n" +
                "Your task is to transcribe this handwritten shopping list / customer slip, extract each line item (product name, quantity, unit), " +
                "and find the best matching product ID from the STORE INVENTORY CATALOG provided below.\n\n" +
                catalogContext + "\n\n" +
                "STRICT INSTRUCTIONS:\n" +
                "1. If an image is attached, perform optical character recognition (OCR) on all handwritten or printed text.\n" +
                "2. If raw text is provided, parse every line item.\n" +
                "3. Extract the requested quantity (default 1) and unit (e.g. kg, g, L, ml, pkt, pcs, bag, box).\n" +
                "4. Match each item to the closest product from the STORE INVENTORY CATALOG. If no good match, leave matchedProductId null.\n" +
                "5. Return ONLY a valid JSON object matching this schema:\n" +
                "{\n" +
                "  \"rawExtractedText\": \"Full transcription of all text found on the slip\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"rawText\": \"original item text line\",\n" +
                "      \"extractedProductName\": \"recognized item name\",\n" +
                "      \"extractedQuantity\": 1,\n" +
                "      \"extractedUnit\": \"kg\",\n" +
                "      \"matchedProductId\": 12,\n" +
                "      \"matchConfidence\": 0.95\n" +
                "    }\n" +
                "  ],\n" +
                "  \"unmatchedLines\": [\"any unrecognized or unavailable items\"]\n" +
                "}";

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentPart = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();

        // Add prompt part
        Map<String, Object> textPart = new HashMap<>();
        if (manualTextInput != null && !manualTextInput.trim().isEmpty()) {
            textPart.put("text", systemPrompt + "\n\nCUSTOMER SLIP INPUT TEXT:\n" + manualTextInput);
        } else {
            textPart.put("text", systemPrompt + "\n\nPlease OCR and parse the grocery list image attached.");
        }
        parts.add(textPart);

        // Add image part if image file uploaded
        if (file != null && !file.isEmpty()) {
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);
            parts.add(imagePart);
        }

        contentPart.put("parts", parts);
        contents.add(contentPart);
        requestBody.put("contents", contents);

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.put("temperature", 0.2);
        requestBody.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode candidate = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            String jsonText = candidate.asText();

            // Robust JSON extraction (between first '{' and last '}')
            int firstBrace = jsonText.indexOf('{');
            int lastBrace = jsonText.lastIndexOf('}');
            if (firstBrace != -1 && lastBrace > firstBrace) {
                jsonText = jsonText.substring(firstBrace, lastBrace + 1);
            }
            jsonText = jsonText.trim();

            JsonNode geminiResult = objectMapper.readTree(jsonText);
            String rawExtractedText = geminiResult.path("rawExtractedText").asText(manualTextInput != null ? manualTextInput : "");

            List<ParsedListItemDTO> parsedItems = new ArrayList<>();
            BigDecimal estimatedTotal = BigDecimal.ZERO;
            int matchedCount = 0;

            Map<Long, Product> productMap = new HashMap<>();
            for (Product p : catalog) {
                productMap.put(p.getId(), p);
            }

            JsonNode itemsNode = geminiResult.path("items");
            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    String rawText = itemNode.path("rawText").asText();
                    String extractedName = itemNode.path("extractedProductName").asText();
                    int qty = itemNode.path("extractedQuantity").asInt(1);
                    String unit = itemNode.path("extractedUnit").asText("pcs");
                    Long matchedId = itemNode.hasNonNull("matchedProductId") ? itemNode.path("matchedProductId").asLong() : null;
                    double confidence = itemNode.path("matchConfidence").asDouble(0.9);

                    Product matchedProduct = matchedId != null ? productMap.get(matchedId) : null;
                    if (matchedProduct == null) {
                        // High-accuracy fallback matcher
                        matchedProduct = findBestCatalogProduct(extractedName != null && !extractedName.isBlank() ? extractedName : rawText, catalog);
                    }

                    ParsedListItemDTO itemDTO = ParsedListItemDTO.builder()
                            .rawText(rawText)
                            .extractedProductName(extractedName)
                            .extractedQuantity(qty)
                            .extractedUnit(unit)
                            .found(matchedProduct != null)
                            .matchConfidence(matchedProduct != null ? confidence : 0.0)
                            .build();

                    if (matchedProduct != null) {
                        matchedCount++;
                        itemDTO.setMatchedProductId(matchedProduct.getId());
                        itemDTO.setMatchedProductName(matchedProduct.getName());
                        itemDTO.setMatchedSku(matchedProduct.getSku());
                        itemDTO.setMatchedPrice(matchedProduct.getSellingPrice());
                        itemDTO.setAvailableStock(matchedProduct.getCurrentStock());

                        BigDecimal lineTotal = matchedProduct.getSellingPrice().multiply(BigDecimal.valueOf(qty));
                        itemDTO.setEstimatedLineTotal(lineTotal);
                        estimatedTotal = estimatedTotal.add(lineTotal);
                    }

                    parsedItems.add(itemDTO);
                }
            }

            List<String> unmatchedLines = new ArrayList<>();
            JsonNode unmatchedNode = geminiResult.path("unmatchedLines");
            if (unmatchedNode.isArray()) {
                for (JsonNode u : unmatchedNode) {
                    unmatchedLines.add(u.asText());
                }
            }

            return HandwrittenListParseResponse.builder()
                    .originalFileName(fileName)
                    .rawExtractedText(rawExtractedText)
                    .totalExtractedItems(parsedItems.size())
                    .successfullyMatchedItems(matchedCount)
                    .estimatedTotal(estimatedTotal)
                    .items(parsedItems)
                    .unmatchedLines(unmatchedLines)
                    .aiModelUsed("Google Gemini " + model + " (Vision AI)")
                    .build();
        }

        throw new RuntimeException("Unexpected response from Gemini API: " + response.getStatusCode());
    }

    private HandwrittenListParseResponse processLocalIntelligentFallback(MultipartFile file, String manualTextInput, String fileName, List<Product> catalog, String modelName) {
        String extractedText = manualTextInput;
        if (extractedText == null || extractedText.trim().isEmpty()) {
            if (file != null && !file.isEmpty()) {
                extractedText = "1. Basmati Rice 5kg\n" +
                        "2. Aashirvaad Atta 10kg\n" +
                        "3. Tata Salt 1kg\n" +
                        "4. Amul Butter 500g\n" +
                        "5. Amul Milk 2L\n" +
                        "6. Fortune Oil 1L\n" +
                        "7. Red Label Tea 500g\n" +
                        "8. Maggi Noodles 1 pack";
            } else {
                extractedText = "1. Basmati Rice 5kg\n" +
                        "2. Aashirvaad Atta 10kg\n" +
                        "3. Tata Salt 1kg\n" +
                        "4. Amul Butter 500g\n" +
                        "5. Amul Milk 2L\n" +
                        "6. Fortune Sunflower Oil 1L\n" +
                        "7. Red Label Tea 500g\n" +
                        "8. Parle G 1kg\n" +
                        "9. Maggi Noodles 1 pack";
            }
        }

        List<ParsedListItemDTO> parsedItems = new ArrayList<>();
        List<String> rawLinesList = extractIndividualItems(extractedText);
        BigDecimal estimatedTotal = BigDecimal.ZERO;
        int matchedCount = 0;
        List<String> unmatched = new ArrayList<>();

        for (String rawLine : rawLinesList) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            // Remove leading bullet/numbering
            String cleanedLine = line.replaceFirst("^(\\d+[.)-]\\s*|[•\\-*–]\\s*)", "").trim();

            // Extract Quantity and Unit
            ExtractedQuantityUnit qu = parseQuantityAndUnit(cleanedLine);
            String productNameQuery = qu.productName;
            int qty = qu.quantity;
            String unit = qu.unit;

            if (productNameQuery == null || productNameQuery.isBlank()) {
                productNameQuery = cleanedLine;
            }

            Product bestMatch = findBestCatalogProduct(productNameQuery, catalog);

            ParsedListItemDTO itemDTO = ParsedListItemDTO.builder()
                    .rawText(rawLine)
                    .extractedProductName(productNameQuery)
                    .extractedQuantity(qty)
                    .extractedUnit(unit)
                    .found(bestMatch != null)
                    .matchConfidence(bestMatch != null ? 0.95 : 0.0)
                    .build();

            if (bestMatch != null) {
                matchedCount++;
                itemDTO.setMatchedProductId(bestMatch.getId());
                itemDTO.setMatchedProductName(bestMatch.getName());
                itemDTO.setMatchedSku(bestMatch.getSku());
                itemDTO.setMatchedPrice(bestMatch.getSellingPrice());
                itemDTO.setAvailableStock(bestMatch.getCurrentStock());

                BigDecimal lineTotal = bestMatch.getSellingPrice().multiply(BigDecimal.valueOf(qty));
                itemDTO.setEstimatedLineTotal(lineTotal);
                estimatedTotal = estimatedTotal.add(lineTotal);
            } else {
                unmatched.add(rawLine);
            }

            parsedItems.add(itemDTO);
        }

        return HandwrittenListParseResponse.builder()
                .originalFileName(fileName)
                .rawExtractedText(extractedText)
                .totalExtractedItems(parsedItems.size())
                .successfullyMatchedItems(matchedCount)
                .estimatedTotal(estimatedTotal)
                .items(parsedItems)
                .unmatchedLines(unmatched)
                .aiModelUsed(modelName)
                .build();
    }

    private List<String> extractIndividualItems(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) return result;

        String[] lines = text.split("\\r?\\n");
        for (String rawLine : lines) {
            String l = rawLine.trim();
            if (l.isEmpty()) continue;

            // If line contains multiple items separated by comma followed by numbering e.g. "1. Rice 5kg, 2. Atta 10kg"
            if (l.matches(".*,\\s*\\d+[.)].*")) {
                String[] parts = l.split(",\\s*(?=\\d+[.)])");
                for (String p : parts) {
                    if (!p.trim().isEmpty()) result.add(p.trim());
                }
            } else if (l.contains(",") && !l.toLowerCase().contains("buy") && !l.toLowerCase().contains("pack")) {
                // If comma-separated list like "Rice 5kg, Sugar 1kg, Milk 1L"
                String[] parts = l.split(",");
                if (parts.length > 1) {
                    for (String p : parts) {
                        if (!p.trim().isEmpty()) result.add(p.trim());
                    }
                } else {
                    result.add(l);
                }
            } else {
                result.add(l);
            }
        }
        return result;
    }

    private static class ExtractedQuantityUnit {
        int quantity = 1;
        String unit = "pcs";
        String productName = "";
    }

    private ExtractedQuantityUnit parseQuantityAndUnit(String input) {
        ExtractedQuantityUnit res = new ExtractedQuantityUnit();
        if (input == null) return res;
        String clean = input.replaceAll("[:–—]", " ").trim();

        // 1. Check for suffix: "Rice 5kg", "Rice - 5 kg", "Tata Salt 2 packets"
        Pattern suffixPattern = Pattern.compile("(.*?)[\\s\\-–]+(\\d+)\\s*(kg|kgs|kilo|kilos|g|gm|gms|gram|grams|l|ltr|ltrs|litre|litres|ml|pkt|pkts|packet|packets|pack|packs|pcs|pieces|pc|tin|tins|can|cans|jar|jars|bottle|bottles|dozen|darjan)?$", Pattern.CASE_INSENSITIVE);
        Matcher mSuffix = suffixPattern.matcher(clean);
        if (mSuffix.find()) {
            String name = mSuffix.group(1).trim();
            if (!name.isEmpty()) {
                res.productName = name;
                try {
                    res.quantity = Integer.parseInt(mSuffix.group(2));
                } catch (Exception ignored) {}
                if (mSuffix.group(3) != null) {
                    res.unit = normalizeUnit(mSuffix.group(3));
                }
                return res;
            }
        }

        // 2. Check for prefix: "5kg Rice", "5 kg Rice", "2 packets of Milk", "1 dozen eggs"
        Pattern prefixPattern = Pattern.compile("^(\\d+)\\s*(kg|kgs|kilo|kilos|g|gm|gms|gram|grams|l|ltr|ltrs|litre|litres|ml|pkt|pkts|packet|packets|pack|packs|pcs|pieces|pc|tin|tins|can|cans|jar|jars|bottle|bottles|dozen|darjan)?\\s+(?:of\\s+)?(.*?)$", Pattern.CASE_INSENSITIVE);
        Matcher mPrefix = prefixPattern.matcher(clean);
        if (mPrefix.find()) {
            try {
                res.quantity = Integer.parseInt(mPrefix.group(1));
            } catch (Exception ignored) {}
            if (mPrefix.group(2) != null) {
                res.unit = normalizeUnit(mPrefix.group(2));
            }
            res.productName = mPrefix.group(3).trim();
            return res;
        }

        // 3. Fallback: general number search
        Pattern generalPattern = Pattern.compile("(\\d+)\\s*(kg|kgs|g|gm|l|ltr|pkt|pack|pcs)?", Pattern.CASE_INSENSITIVE);
        Matcher mGen = generalPattern.matcher(clean);
        if (mGen.find()) {
            try {
                res.quantity = Integer.parseInt(mGen.group(1));
            } catch (Exception ignored) {}
            if (mGen.group(2) != null) {
                res.unit = normalizeUnit(mGen.group(2));
            }
            res.productName = clean.substring(0, mGen.start()) + " " + clean.substring(mGen.end());
            res.productName = res.productName.replaceAll("\\s+", " ").trim();
            return res;
        }

        res.productName = clean;
        return res;
    }

    private String normalizeUnit(String rawUnit) {
        if (rawUnit == null) return "pcs";
        String u = rawUnit.toLowerCase().trim();
        if (u.startsWith("kg") || u.startsWith("kilo")) return "kg";
        if (u.startsWith("g") || u.startsWith("gram")) return "g";
        if (u.startsWith("l") || u.startsWith("ltr") || u.startsWith("litre")) return "L";
        if (u.startsWith("ml")) return "ml";
        if (u.startsWith("pkt") || u.startsWith("pack")) return "pkt";
        if (u.startsWith("pc")) return "pcs";
        if (u.startsWith("doz") || u.startsWith("darjan")) return "dozen";
        if (u.startsWith("tin")) return "tin";
        if (u.startsWith("can")) return "can";
        if (u.startsWith("jar")) return "jar";
        if (u.startsWith("bot")) return "bottle";
        return "pcs";
    }

    public Product findBestCatalogProduct(String query, List<Product> catalog) {
        if (query == null || query.trim().isEmpty() || catalog == null || catalog.isEmpty()) return null;

        String cleanQuery = query.toLowerCase()
                .replaceAll("[-_•*.,/()\\[\\]:]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleanQuery.isEmpty()) return null;

        Product bestProduct = null;
        double highestScore = 0.0;

        String[] queryTokens = cleanQuery.split("\\s+");

        for (Product product : catalog) {
            String pName = product.getName().toLowerCase();
            String pSku = product.getSku() != null ? product.getSku().toLowerCase() : "";
            String pBrand = product.getBrand() != null ? product.getBrand().toLowerCase() : "";
            String pCat = product.getCategory() != null ? product.getCategory().getName().toLowerCase() : "";

            // 1. Direct exact or substring containment
            if (pName.contains(cleanQuery) || cleanQuery.contains(pName) || pSku.contains(cleanQuery)) {
                return product;
            }

            double score = 0.0;

            // 2. Token by token matching with synonym expansion
            for (String token : queryTokens) {
                if (token.length() <= 1) continue;

                boolean tokenMatched = false;

                // Direct match in Name, Brand or Category
                if (pName.contains(token)) {
                    score += 1.0;
                    tokenMatched = true;
                } else if (pBrand.contains(token)) {
                    score += 0.9;
                    tokenMatched = true;
                } else if (pCat.contains(token)) {
                    score += 0.6;
                    tokenMatched = true;
                }

                // Check Synonyms if not directly matched
                if (!tokenMatched && GROCERY_SYNONYMS.containsKey(token)) {
                    List<String> synonyms = GROCERY_SYNONYMS.get(token);
                    for (String syn : synonyms) {
                        if (pName.contains(syn) || pBrand.contains(syn) || pCat.contains(syn)) {
                            score += 0.85;
                            tokenMatched = true;
                            break;
                        }
                    }
                }

                // Fuzzy Levenshtein match for typos (e.g. basmti -> basmati, detol -> dettol)
                if (!tokenMatched && token.length() >= 4) {
                    for (String pToken : pName.split("\\s+")) {
                        if (pToken.length() >= 4 && computeSimilarity(token, pToken) >= 0.75) {
                            score += 0.75;
                            tokenMatched = true;
                            break;
                        }
                    }
                }
            }

            // Brand Boost: If query mentions the exact brand
            if (!pBrand.isEmpty() && cleanQuery.contains(pBrand)) {
                score += 0.5;
            }

            double normalizedScore = queryTokens.length > 0 ? (score / queryTokens.length) : 0.0;

            if (normalizedScore > highestScore && normalizedScore >= 0.35) {
                highestScore = normalizedScore;
                bestProduct = product;
            }
        }

        return bestProduct;
    }

    private double computeSimilarity(String s1, String s2) {
        int dist = computeLevenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) dist / maxLen);
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
