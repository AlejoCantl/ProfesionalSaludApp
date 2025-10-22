/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.admisioncitasprofesionalsalud;
import org.json.JSONObject;
import javax.swing.*;

public class PerfilFrame extends JInternalFrame {
    public PerfilFrame(ProfesionalSaludApp parent, String token) {
        super("Perfil del Profesional", true, true, true, true);
        setSize(400, 300);
        cargarPerfil(token);
    }

    private void cargarPerfil(String token) {
        try {
            JSONObject perfil = ApiService.getPerfil(token);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(new JLabel("Nombre: " + perfil.getString("nombre") + " " + perfil.getString("apellido")));
            panel.add(new JLabel("Correo: " + perfil.getString("correo")));
            panel.add(new JLabel("Ubicación: " + perfil.getString("ubicacion")));
            JSONObject datos = perfil.getJSONObject("datos_especificos");
            panel.add(new JLabel("Cargo: " + datos.optString("cargo", "N/A")));
            panel.add(new JLabel("Ingreso: " + datos.optString("fecha_ingreso", "N/A")));

            add(new JScrollPane(panel));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar perfil");
        }
    }
}
