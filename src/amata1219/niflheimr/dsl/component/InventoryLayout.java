package amata1219.niflheimr.dsl.component;

import amata1219.niflheimr.dsl.component.slot.AnimatedSlot;
import amata1219.niflheimr.dsl.component.slot.Slot;
import amata1219.niflheimr.event.InventoryUIClickEvent;
import amata1219.niflheimr.event.InventoryUICloseEvent;
import amata1219.niflheimr.event.InventoryUIOpenEvent;
import amata1219.niflheimr.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class InventoryLayout implements InventoryHolder {

    public final InventoryFormat format;
    public String title;
    private Supplier<Slot> defaultSlot = Slot::new;
    public final HashMap<Integer, Slot> slots = new HashMap<>();
    public final HashMap<Integer, AnimatedSlot> animatedSlots = new HashMap<>();
    public final HashMap<Integer, Icon> currentIcons = new HashMap<>();
    private Consumer<InventoryUIClickEvent> actionOnClick = Constants.noOperation();
    private Consumer<InventoryUIOpenEvent> actionOnOpen = Constants.noOperation();
    private Consumer<InventoryUICloseEvent> actionOnClose = Constants.noOperation();

    public InventoryLayout(InventoryFormat format) {
        this.format = format;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public Inventory buildInventory(){
        Inventory inventory = createInventory(format.type, format.size, title);
        currentIcons.clear();
        for (int i = 0; i < inventory.getSize(); i++) {
            Slot slot;
            if (slots.containsKey(i)) slot = slots.get(i);
            else if (animatedSlots.containsKey(i)) slot = animatedSlots.get(i);
            else slot = defaultSlot.get();

            Icon icon = slot.buildIcon();
            currentIcons.put(i, icon);
            inventory.setItem(i, icon.toItemStack());
        }
        return inventory;
    }

    private Inventory createInventory(InventoryType type, int size, String title) {
        if(type != null){
            if(title != null) return Bukkit.createInventory(this, type, title);
            else return Bukkit.createInventory(this, type);
        } else {
            if(title != null) return Bukkit.createInventory(this, size, title);
            else return Bukkit.createInventory(this, size);
        }
    }

    public void defaultSlot(Consumer<Slot> settings) {
        defaultSlot = () -> {
            Slot slot = new Slot();
            settings.accept(slot);
            return slot;
        };
    }

    public Slot getSlotAt(int index) {
        return slots.getOrDefault(index, animatedSlots.containsKey(index) ? animatedSlots.get(index) : defaultSlot.get());
    }

    public void putSlot(Consumer<Slot> settings, int... indexes) {
        for (int index : indexes) {
            Slot slot = new Slot();
            settings.accept(slot);
            slots.put(index, slot);
        }
    }

    public void putSlot(Consumer<Slot> settings, IntStream indexes) {
        putSlot(settings, indexes.toArray());
    }

    public void putAnimatedSlot(Consumer<AnimatedSlot> settings, int... indexes) {
        for (int index : indexes) {
            AnimatedSlot slot = new AnimatedSlot();
            settings.accept(slot);
            animatedSlots.put(index, slot);
        }
    }

    public void putAnimatedSlot(Consumer<AnimatedSlot> settings, IntStream indexes) {
        putAnimatedSlot(settings, indexes.toArray());
    }

    public Consumer<InventoryUIClickEvent>  actionOnClick() {
        return actionOnClick;
    }

    public void onClick(Consumer<InventoryUIClickEvent> actionOnClick) {
        this.actionOnClick = actionOnClick;
    }

    public Consumer<InventoryUIOpenEvent>  actionOnOpen() {
        return actionOnOpen;
    }

    public void onOpen(Consumer<InventoryUIOpenEvent> actionOnOpen) {
        this.actionOnOpen = actionOnOpen;
    }

    public Consumer<InventoryUICloseEvent>  actionOnClose() {
        return actionOnClose;
    }

    public void onClose(Consumer<InventoryUICloseEvent> actionOnClose) {
        this.actionOnClose = actionOnClose;
    }

}
