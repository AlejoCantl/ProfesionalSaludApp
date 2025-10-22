/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// utils/SwingUtils.java
package com.mycompany.admisioncitasprofesionalsalud.utils;

import javax.swing.*;
import java.awt.*;

public class SwingUtils {
    public static void centrarEnDesktop(JInternalFrame frame, JDesktopPane desktop) {
        // Asegurarse de que el frame tenga tamaño
        frame.pack();
        if (frame.getWidth() == 0 || frame.getHeight() == 0) {
            frame.setSize(400, 300); // fallback
        }

        Dimension desktopSize = desktop.getSize();
        Dimension frameSize = frame.getSize();

        int x = (desktopSize.width - frameSize.width) / 2;
        int y = (desktopSize.height - frameSize.height) / 2;

        // Asegurarse de que no se salga de los bordes
        x = Math.max(0, x);
        y = Math.max(0, y);

        frame.setLocation(x, y);
    }
}