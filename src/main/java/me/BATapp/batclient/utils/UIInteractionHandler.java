package me.BATapp.batclient.utils;

import net.minecraft.client.MinecraftClient;

/**
 * Handler pro drag a resize UI elementů
 */
public class UIInteractionHandler {
    private static UIElementManager.UIElement draggingElement;
    private static boolean isResizing = false;

    public static void onMouseMove(int mouseX, int mouseY) {
        if (draggingElement != null) {
            if (isResizing) {
                draggingElement.updateResize(mouseX, mouseY);
            } else if (draggingElement.dragging) {
                draggingElement.updateDrag(mouseX, mouseY);
            }
        }
    }

    public static void onMouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return; // Only left mouse button
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return; // Don't interact when screen is open
        
        // Check all UI elements
        for (UIElementManager.UIElement elem : UIElementManager.getAllElements()) {
            if (elem.isMouseOver(mouseX, mouseY)) {
                draggingElement = elem;
                
                if (elem.isResizeHandleOver(mouseX, mouseY)) {
                    elem.startResize(mouseX, mouseY);
                    isResizing = true;
                } else {
                    elem.startDrag(mouseX, mouseY);
                    isResizing = false;
                }
                break;
            }
        }
    }

    public static void onMouseUp(int mouseX, int mouseY, int button) {
        if (button != 0) return;
        
        if (draggingElement != null) {
            draggingElement.stopDrag();
            draggingElement.stopResize();
            UIElementManager.savePositions();
            draggingElement = null;
            isResizing = false;
        }
    }

    public static UIElementManager.UIElement getDraggingElement() {
        return draggingElement;
    }

    public static boolean isDragging() {
        return draggingElement != null;
    }
}
