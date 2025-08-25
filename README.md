# TherapyNest
An AI-enhanced journal to track mental well-being, built with Java Spring Boot.


# 🌱 TherapyNest: AI-Powered Mental Health Journal  

**A smart journaling companion that tracks your emotions, analyzes your thoughts, and suggests personalized mental health resources—powered by Java Spring Boot and AI.**  

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)  
[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://openjdk.org/)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1%2B-brightgreen)](https://spring.io/projects/spring-boot)  

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
   git clone https://github.com/johnmalugu/therapynest.git  
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

## 🤝 Contribute  
We welcome PRs! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.  

## 📜 License  
MIT © 2025 johnmalugu  

---

### Key Notes for GitHub:  
1. **Badges**: Use [shields.io](https://shields.io/) for tech stack badges (like Java/Spring Boot versions).  
2. **Screenshots**: Add mockups or diagrams later (e.g., mood charts).  
3. **Roadmap**: Link to a `PROJECT_ROADMAP.md` for AI integration plans.  

Want to add a demo GIF or more detailed setup instructions? Let me know!
