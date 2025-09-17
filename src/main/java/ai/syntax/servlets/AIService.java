package ai.syntax.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import ai.syntax.service.GeminiAIService;

@WebServlet("/AIService")
public class AIService extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Gson gson = new Gson();
    private GeminiAIService geminiAIService;

    @Override
    public void init() throws ServletException {
        super.init();
        geminiAIService = new GeminiAIService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String originalCode = request.getParameter("code");
        String language = request.getParameter("language");
        String analysisMode = request.getParameter("analysisMode");

        System.out.println("--- DEBUGGING originalCode in Servlet ---");
        System.out.println("originalCode raw value: \"" + originalCode + "\"");
        System.out.println("originalCode is null: " + (originalCode == null));
        System.out.println("originalCode length: " + (originalCode != null ? originalCode.length() : "N/A"));
        System.out.println("originalCode.trim() length: " + (originalCode != null ? originalCode.trim().length() : "N/A"));
        System.out.println("originalCode.trim().isEmpty(): " + (originalCode != null ? originalCode.trim().isEmpty() : "N/A"));
        System.out.println("----------------------------------------");

        System.out.println("--- AI Service Request Received ---");
        System.out.println("Language: " + (language != null && !language.isEmpty() ? language : "[Not specified]"));
        System.out.println("Analysis Mode: " + analysisMode);
        System.out.println("Original Code (first 50 chars):\n" + (originalCode != null ? originalCode.substring(0, Math.min(originalCode.length(), 50)) + "..." : "[Empty Code]"));
        System.out.println("---------------------------------");

        String aiResponse = "";
        String statusMessage = "";
        boolean success = true;

        if (originalCode == null || originalCode.trim().isEmpty()) {
            aiResponse = "Please provide code for analysis.";
            statusMessage = "No code provided.";
            success = false;
        } else if (analysisMode == null || analysisMode.isEmpty()) {
            aiResponse = "Error: Analysis mode not specified.";
            statusMessage = "Please select an analysis mode.";
            success = false;
        } else {
            try {
                aiResponse = geminiAIService.getGeminiAnalysis(originalCode, language, analysisMode);
                statusMessage = "AI analysis complete!";
                success = true;
            } catch (Exception e) {
                System.err.println("Error calling Gemini AI Service: " + e.getMessage());
                e.printStackTrace();
                aiResponse = "Error: Could not connect to AI service or process request. Check server logs.";
                statusMessage = "Analysis failed due to server error.";
                success = false;
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> jsonResponseMap = new HashMap<>();
        jsonResponseMap.put("success", success);
        jsonResponseMap.put("message", statusMessage);
        jsonResponseMap.put("originalCode", originalCode);
        jsonResponseMap.put("analysisMode", analysisMode);
        jsonResponseMap.put("aiResponse", aiResponse);

        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(jsonResponseMap));
            out.flush();
        } catch (IOException e) {
            System.err.println("Error writing JSON response: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error sending response.");
        }
    }
}