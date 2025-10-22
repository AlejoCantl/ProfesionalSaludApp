/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.admisioncitasprofesionalsalud;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8000";

    public static String login(String usuario, String contrasena) throws Exception {
        String url = BASE_URL + "/Usuario/login";
        JSONObject body = new JSONObject();
        body.put("nombre_usuario", usuario);
        body.put("contrasena", contrasena);
        String response = post(url, body.toString(), null);
        return new JSONObject(response).getString("access_token");
    }

    public static int getUsuarioId(String token) throws Exception {
        JSONObject perfil = getPerfil(token);
        return perfil.getInt("id"); // Ajustar si el campo es diferente
    }

    public static JSONObject getPerfil(String token) throws Exception {
        return new JSONObject(get(BASE_URL + "/Usuario/perfil", token));
    }

    public static JSONArray getCitasPendientes(String token) throws Exception {
        JSONObject res = new JSONObject(get(BASE_URL + "/Profesional_salud/citas", token));
        return res.getJSONArray("citas");
    }

    public static JSONObject getDetalleCita(String token, int citaId) throws Exception {
        JSONObject res = new JSONObject(get(BASE_URL + "/Profesional_salud/citas/" + citaId + "/detalle", token));
        return res.getJSONObject("detalle_cita");
    }

    public static void aprobarCita(String token, int citaId, boolean aprobado, String razon) {
        try {
            String url = BASE_URL + "/Profesional_salud/citas/aprobar";
            JSONObject body = new JSONObject();
            body.put("cita_id", citaId);
            body.put("aprobado", aprobado);
            if (!aprobado && razon != null && !razon.isEmpty()) {
                body.put("razon", razon);
            }
            post(url, body.toString(), token);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al procesar cita: " + e.getMessage());
        }
    }

    private static String get(String url, String token) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return readResponse(conn);
    }

    private static String post(String url, String json, String token) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        if (token != null) conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(
            conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream()
        ));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) response.append(line);
        br.close();
        if (conn.getResponseCode() >= 400) throw new Exception("HTTP " + conn.getResponseCode() + ": " + response);
        return response.toString();
    }
}
