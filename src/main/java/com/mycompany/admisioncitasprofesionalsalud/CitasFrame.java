package com.mycompany.admisioncitasprofesionalsalud;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CitasFrame extends JInternalFrame {
    private JTable tabla;
    private DefaultTableModel modelo;
    private final ProfesionalSaludApp parent; // Mantener referencia para agregar el frame

    public CitasFrame(ProfesionalSaludApp parent, String token) {
        super("Citas Pendientes", true, true, true, true);
        this.parent = parent;
        
        initUI(parent, token);
        
        // -----------------------------------------------------------------
        // Ajuste de tamaño final y centrado
        setPreferredSize(new Dimension(850, 500)); // Ligeramente más ancho
        pack();
        setMinimumSize(getSize());
        parent.agregarFrame(this); // Agregar y centrar el frame
        // -----------------------------------------------------------------
    }

    private void initUI(ProfesionalSaludApp parent, String token) {
        // Usar BorderLayout para el JInternalFrame
        setLayout(new BorderLayout()); 

        String[] columnas = {"ID", "Paciente", "Identificación", "Médico", "Fecha y Hora", "Especialidad"};
        modelo = new DefaultTableModel(columnas, 0) {
            // Impedir la edición de celdas
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        
        // --- 1. MEJORAS VISUALES Y CENTRADO DE LA TABLA ---
        
        // 1.1 Renderer para CENTRAR TODO EL TEXTO
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Aplicar el renderizador centrado a TODAS las columnas
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // 1.2 Visibilidad de Separadores
        // Mostrar líneas de cuadrícula para filas y columnas
        tabla.setShowGrid(true); 
        // Color de las líneas de la cuadrícula
        tabla.setGridColor(Color.LIGHT_GRAY); 
        
        // Estilo de la cabecera (Header)
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        tabla.getTableHeader().setBackground(new Color(220, 220, 220)); // Gris claro para el fondo
        tabla.getTableHeader().setForeground(new Color(50, 50, 50)); // Gris oscuro para el texto
        tabla.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Borde más visible

        // Altura de las filas y de la cabecera
        tabla.setRowHeight(25);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 30)); 

        // Selección de fila completa y solo una
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Ajuste de ancho (ejemplo: ID más pequeño)
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40); 
        
        // Añadir la tabla al centro
        JScrollPane scrollPane = new JScrollPane(tabla);
        // Añadir un margen (borde vacío) alrededor de la tabla
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 10, 0, 10), 
            scrollPane.getBorder()
        ));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // --- 2. Panel de Botones ---
        
        JButton btnDetalle = new JButton("Ver Detalles de la cita");
        btnDetalle.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                int citaId = (int) modelo.getValueAt(row, 0); 
                new DetalleCitaFrame(parent, token, citaId);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una cita", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        botonPanel.add(btnDetalle);
        add(botonPanel, BorderLayout.SOUTH);
        
        // Cargar datos
        cargarCitas(token);
    }

    public void cargarCitas(String token) {
        try {
            JSONArray citas = ApiService.getCitasPendientes(token);
            modelo.setRowCount(0);
            for (int i = 0; i < citas.length(); i++) {
                JSONObject c = citas.getJSONObject(i);
                modelo.addRow(new Object[]{
                    c.getInt("id"), 
                    c.getString("paciente"), 
                    c.getString("identificacion"), 
                    c.getString("medico"), 
                    c.getString("fecha") + " "+ c.getString("hora"), 
                    c.getString("especialidad")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + e.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
            // Si es necesario para depuración, puedes descomentar:
            // e.printStackTrace(); 
        }
    }
}