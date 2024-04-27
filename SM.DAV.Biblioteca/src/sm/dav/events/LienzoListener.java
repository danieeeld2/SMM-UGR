/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sm.dav.events;

import sm.dav.events.LienzoEvent;
import java.util.EventListener;

/**
 *
 * @author daniel
 */
public interface LienzoListener extends EventListener {
    public void shapeAdded(LienzoEvent evt);
    public void shapeSelected(LienzoEvent evt);
}
