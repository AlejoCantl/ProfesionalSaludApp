package com.mycompany.admisioncitasprofesionalsalud;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.JOptionPane;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8000";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ========================================
    // MÉTODOS PÚBLICOS (iguales a antes)
    // ========================================

    public static String login(String usuario, String contrasena) throws Exception {
        JSONObject body = new JSONObject();
        body.put("nombre_usuario", usuario);
        body.put("contrasena", contrasena);
        String response = post("/Usuario/login", body.toString(), null);
        return new JSONObject(response).getString("access_token");
    }

    public static int getUsuarioId(String token) throws Exception {
        JSONObject perfil = getPerfil(token);
        return perfil.getInt("id");
    }

    public static JSONObject getPerfil(String token) throws Exception {
        String response = get("/Usuario/perfil", token);
        return new JSONObject(response);
    }

    public static JSONArray getCitasPendientes(String token) throws Exception {
        JSONObject res = new JSONObject(get("/Profesional_salud/citas", token));
        return res.getJSONArray("citas");
    }

    public static JSONObject getDetalleCita(String token, int citaId) throws Exception {
        JSONObject res = new JSONObject(get("/Profesional_salud/citas/" + citaId + "/detalle", token));
        return res.getJSONObject("detalle_cita");
    }

    public static void aprobarCita(String token, int citaId, boolean aprobado, String razon) {
        try {
            JSONObject body = new JSONObject();
            body.put("cita_id", citaId);
            body.put("aprobado", aprobado);
            if (!aprobado && razon != null && !razon.trim().isEmpty()) {
                body.put("razon", razon.trim());
            }
            // Usa PATCH (ahora funciona!)
            patch("/Profesional_salud/citas/aprobar", body.toString(), token);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al procesar cita: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Decodificar rol

    public static int getRolId(String token) throws Exception {
        // 1. Separar el token en sus 3 partes (header.payload.signature)
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new Exception("Token JWT inválido.");
        }

        // 2. Decodificar el payload (base64url)
        String payloadBase64 = parts[1];
        // Rellena el padding si es necesario
        String payload = new String(java.util.Base64.getUrlDecoder().decode(payloadBase64.getBytes()), "UTF-8");

        // 3. Parsear el JSON
        JSONObject json = new JSONObject(payload);

        // 4. Extraer el rol_id
        if (!json.has("rol_id")) {
            throw new Exception("El token no contiene el ID de rol.");
        }

        return json.getInt("rol_id");
    }

    // ========================================
    // MÉTODOS HTTP GENÉRICOS (HttpClient)
    // ========================================

    private static String get(String path, String token) throws Exception {
        return sendRequest("GET", path, null, token);
    }

    private static String post(String path, String json, String token) throws Exception {
        return sendRequest("POST", path, json, token);
    }

    private static String patch(String path, String json, String token) throws Exception {
        return sendRequest("PATCH", path, json, token);
    }

    private static String put(String path, String json, String token) throws Exception {
        return sendRequest("PUT", path, json, token);
    }

    private static String delete(String path, String token) throws Exception {
        return sendRequest("DELETE", path, null, token);
    }

    // ========================================
    // MÉTODO GENÉRICO (reutilizable)
    // ========================================

    private static String sendRequest(String method, String path, String jsonBody, String token) throws Exception {
        String url = BASE_URL + path;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15));

        // Headers
        builder.header("Content-Type", "application/json");
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        // Método y cuerpo
        if (jsonBody != null && !jsonBody.trim().isEmpty()) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(jsonBody));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpRequest request = builder.build();

        // Enviar y manejar respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new Exception("HTTP " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    // ========================================
    // MÉTODO ASÍNCRONO (opcional)
    // ========================================

    public static CompletableFuture<String> getAsync(String path, String token) {
        String url = BASE_URL + path;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() >= 400) {
                        throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
                    }
                    return resp.body();
                });
    }
}