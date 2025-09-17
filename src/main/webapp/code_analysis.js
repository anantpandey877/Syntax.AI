document.addEventListener('DOMContentLoaded', () => {
    const analysisForm = document.getElementById('analysisForm');
    const statusMessageDiv = document.getElementById('statusMessage');
    const loadingMessageDiv = document.getElementById('loadingMessage');
    const resultsAreaDiv = document.getElementById('resultsArea');
    const displayAnalysisMode = document.getElementById('displayAnalysisMode');
    const displayOriginalCode = document.getElementById('displayOriginalCode');
    const displayAIResponse = document.getElementById('displayAIResponse');

    const urlParams = new URLSearchParams(window.location.search);
    const status = urlParams.get('status');
    const message = urlParams.get('message');
    if (status && message) {
        statusMessageDiv.textContent = decodeURIComponent(message);
        statusMessageDiv.style.display = 'block';
        statusMessageDiv.classList.add(status);
    }

   
    analysisForm.addEventListener('submit', async (event) => {
        event.preventDefault(); 

        statusMessageDiv.style.display = 'none'; 
        resultsAreaDiv.style.display = 'none'; 
        loadingMessageDiv.style.display = 'block'; 

        const formData = new FormData(analysisForm);

       
        let selectedAnalysisMode = formData.get('analysisMode');
        if (!selectedAnalysisMode) { 
            selectedAnalysisMode = 'bug_fix'; 
            formData.set('analysisMode', selectedAnalysisMode); 
        }
       
        const requestBody = new URLSearchParams(formData).toString();

        try {
           
            const response = await fetch('AIService', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded' 
                },
                body: requestBody 
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const result = await response.json(); 
            loadingMessageDiv.style.display = 'none'; 

            if (result.success) {
                displayAnalysisMode.textContent = result.analysisMode || 'N/A';
                displayOriginalCode.textContent = result.originalCode || 'No code submitted.';
                displayAIResponse.textContent = result.aiResponse || 'No AI response.';
                resultsAreaDiv.style.display = 'block'; 
				
                statusMessageDiv.textContent = 'Analysis complete!';
                statusMessageDiv.className = 'message success';
                statusMessageDiv.style.display = 'block';
            } else {
                statusMessageDiv.textContent = result.message || 'An error occurred during analysis.';
                statusMessageDiv.className = 'message error';
                statusMessageDiv.style.display = 'block';
            }

        } catch (error) {
            loadingMessageDiv.style.display = 'none'; 
            console.error('Error during code analysis:', error);
            statusMessageDiv.textContent = 'Failed to connect to server or process analysis. Please try again.';
            statusMessageDiv.className = 'message error';
            statusMessageDiv.style.display = 'block';
        }
    }
	);
});