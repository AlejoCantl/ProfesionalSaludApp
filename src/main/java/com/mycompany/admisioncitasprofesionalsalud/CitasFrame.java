/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.admisioncitasprofesionalsalud;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CitasFrame extends JInternalFrame {
    private JTable tabla;
    private DefaultTableModel modelo;

    public CitasFrame(ProfesionalSaludApp parent, String token) {
        super("Citas Pendientes", true, true, true, true);
        setSize(600, 400);
        initUI(parent, token);
    }

    private void initUI(ProfesionalSaludApp parent, String token) {
        String[] columnas = {"ID", "Paciente", "Fecha"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnDetalle = new JButton("Ver Detalle y Aprobar");
        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                int citaId = (int) modelo.getValueAt(row, 0);
                new DetalleCitaFrame(parent, token, citaId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una cita");
            }
        });
        add(btnDetalle, BorderLayout.SOUTH);

        cargarCitas(token);
    }

    private void cargarCitas(String token) {
        try {
            JSONArray citas = ApiService.getCitasPendientes(token);
            modelo.setRowCount(0);
            for (int i = 0; i < citas.length(); i++) {
                JSONObject c = citas.getJSONObject(i);
                modelo.addRow(new Object[]{c.getInt("id"), c.getString("paciente"), c.getString("fecha")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas");
        }
    }
}