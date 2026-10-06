# Voice & Vision Multimodal AI Assistant

A modular Java 21 CLI assistant with a FRIDAY-inspired conversational persona, Gemini-compatible multimodal vision requests, webcam snapshots, microphone recording, and SQLite chat persistence.

## Requirements

- JDK 21+
- Maven 3.9+
- A webcam for `/snap` and a microphone for `/record`
- A Gemini API key (the API has its own quotas and terms; this project does not provide a key)

## Configuration

Set the API key as an environment variable:

```bash
export GEMINI_API_KEY="your-key"
```

On Windows PowerShell:

```powershell
$env:GEMINI_API_KEY="your-key"
```

The remaining defaults are in `src/main/resources/application.properties`. `DB_PATH` defaults to `data/assistant.db`. `GEMINI_ENDPOINT` can be changed to another compatible endpoint. Optional `STT_ENDPOINT` and `TTS_ENDPOINT` settings are reserved for deployments that provide compatible speech services; the baseline implementation records and plays standard WAV audio locally through Java Sound.

## Run

```bash
mvn clean compile exec:java
```

The first run creates the SQLite database and schema automatically.

## CLI commands

- `/new <title> [custom prompt]` creates and activates a session.
- `/list` lists sessions.
- `/switch <id>` activates an existing session.
- `/delete <id>` deletes a session and its messages.
- `/snap` captures the next webcam image and attaches it to the next prompt.
- `/record [seconds]` records microphone input to `data/latest-recording.wav`.
- `/quit` exits.

Normal text is persisted as a user message, sent with recent context and an optional image, and then persisted as an assistant message.

## Architecture

- `config`: environment and properties configuration.
- `model`: immutable session/message records.
- `dao`: JDBC repositories for sessions and messages.
- `service`: database initialization, API client, camera, audio, and chat orchestration.
- `ui`: interactive console entry point.

The database schema is documented in `src/main/resources/schema.sql`. Image bytes are intentionally sent in-memory for the request and stored as the logical reference `camera://latest`; this avoids silently storing sensitive camera data in SQLite. Production deployments should replace that reference with an encrypted object-store or filesystem repository with retention controls.

## Privacy and safety notes

Camera and microphone access is explicit and local. Review API provider retention and privacy terms before sending images or recordings. The FRIDAY configuration is a writing/persona style and does not clone or reproduce a particular actor's voice.
