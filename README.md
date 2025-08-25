# TherapyJournal
An AI-enhanced journal to track mental well-being, built with Java Spring Boot.


# 🌱 TherapyJournal: AI-Powered Mental Health Journal  

**A smart journaling companion that tracks your emotions, analyzes your thoughts, and suggests personalized mental health resources—powered by Java Spring Boot and AI.**    

## ✨ Features  
- **📝 Smart Journaling**: Write entries, tag moods (e.g., "anxious," "happy"), and get AI-driven insights.  
- **📊 Mood Trends**: Visualize emotional patterns over time with dynamic charts.  
- **🤖 AI Analysis**: Sentiment detection, keyword extraction, and personalized suggestions (e.g., "Try a 5-minute meditation").  
- **🔒 Privacy-First**: End-to-end encryption for sensitive entries.  

## 🛠 Tech Stack  
- **Backend**: Java 21, Spring Boot 3 (REST APIs,GraphQL APIs, JWT auth), PostgreSQL  
- **AI**: Hugging Face Transformers (BERT for sentiment analysis) *(Future Phase)*  
- **Frontend**: React.js (or Flutter for mobile) *(Planned)*  
- **DevOps**: Docker, GitHub Actions  

## 🚀 Getting Started  
1. **Clone the repo**:  
   ```bash  
   git clone https://github.com/jarora2521/TherapyJournal.git  
   ```  
2. **Run the backend**:  
   ```bash  
   cd backend  
   ./mvnw spring-boot:run  
   ```  
3. **API Docs**: Interactive Swagger UI at `http://localhost:8080/swagger-ui.html`.  

## 📌 Example API Endpoints  
| Endpoint                | Description                          |  
|-------------------------|--------------------------------------|  
| `POST /api/journals`    | Submit a journal entry               |  
| `GET /api/moods`        | Fetch mood history for analytics     |  
| `POST /api/ai/analyze`  | *(Future)* Get AI sentiment analysis |  

## 🌟 Why This Project?  
- **Mental Health Matters**: 1 in 5 adults experience mental illness yearly ([NAMI](https://www.nami.org/)). This app bridges gaps in early support.  
- **Tech + Empathy**: Combines cutting-edge AI with human-centered design.  
- **Open Source**: Built to be modular, extensible, and privacy-focused.  
