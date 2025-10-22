/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.admisioncitasprofesionalsalud;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;

public class DetalleCitaFrame extends JInternalFrame {
    public DetalleCitaFrame(ProfesionalSaludApp parent, String token, int citaId) {
        super("Detalle Cita #" + citaId, true, true, true, true);
        setSize(420, 500);
        cargarDetalle(token, citaId, parent);
    }

    private void cargarDetalle(String token, int citaId, ProfesionalSaludApp parent) {
        try {
            JSONObject detalle = ApiService.getDetalleCita(token, citaId);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(new JLabel("<html><b>Paciente:</b> " + detalle.getString("nombre") + " " + detalle.getString("apellido") + "</html>"));
            panel.add(new JLabel("Correo: " + detalle.getString("correo")));
            panel.add(new JLabel("Tipo: " + detalle.getString("tipo_paciente")));
            panel.add(new JLabel("Peso: " + detalle.getDouble("peso") + " kg"));
            panel.add(new JLabel("Altura: " + detalle.getDouble("altura") + " cm"));
            panel.add(new JLabel("Enfermedades: " + detalle.getString("enfermedades")));

            JTextArea txtRazon = new JTextArea("Razón del rechazo (opcional)", 3, 30);
            JButton btnAprobar = new JButton("Aprobar Cita");
            JButton btnRechazar = new JButton("Rechazar Cita");

            btnAprobar.addActionListener(e -> {
                ApiService.aprobarCita(token, citaId, true, null);
                JOptionPane.showMessageDialog(this, "Cita aprobada. Correo enviado.");
                dispose();
            });

            btnRechazar.addActionListener(e -> {
                String razon = txtRazon.getText();
                if (razon.trim().equals("Razón del rechazo (opcional)")) razon = "";
                ApiService.aprobarCita(token, citaId, false, razon);
                JOptionPane.showMessageDialog(this, "Cita rechazada.");
                dispose();
            });

            JPanel botones = new JPanel();
            botones.add(btnAprobar);
            botones.add(btnRechazar);
            panel.add(new JScrollPane(txtRazon));
            panel.add(botones);

            add(panel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
