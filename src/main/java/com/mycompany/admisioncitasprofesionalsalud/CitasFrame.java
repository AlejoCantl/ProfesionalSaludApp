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
        String[] columnas = {"ID", "Paciente", "Identificación","Medico", "Fecha y Hora", "Especialidad"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnDetalle = new JButton("Ver Detalle y Aprobar");
        // dentro de CitasFrame, en el ActionListener del botón:
        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                int citaId = (int) modelo.getValueAt(row, 0);
                // No llamamos a setVisible aquí → el constructor ya lo hace vía agregarFrame
                new DetalleCitaFrame(parent, token, citaId);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una cita");
            }
        });
        add(btnDetalle, BorderLayout.SOUTH);
        cargarCitas(token);
    }

    public void cargarCitas(String token) {
        try {
            JSONArray citas = ApiService.getCitasPendientes(token);
            modelo.setRowCount(0);
            for (int i = 0; i < citas.length(); i++) {
                JSONObject c = citas.getJSONObject(i);
                modelo.addRow(new Object[]{c.getInt("id"), c.getString("paciente"), c.getString("identificacion"), c.getString("medico"), c.getString("fecha") + " "+ c.getString("hora"), c.getString("especialidad")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas");
            System.out.println(e.fillInStackTrace());
        }
    }
}