package daybreak.nerfedshield.config.wizard;

import daybreak.nerfedshield.config.Configuration;
import daybreak.nerfedshield.util.minecraft.ItemBuilder;
import daybreak.nerfedshield.util.minecraft.MaterialX;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class SettingWizard {

	protected static final ItemStack DECO = new ItemBuilder()
			.type(MaterialX.GRAY_STAINED_GLASS_PANE)
			.displayName(ChatColor.WHITE.toString())
			.build();
	static final Logger logger = Logger.getLogger(SettingWizard.class.getName());
	final Player player;
	private final int inventorySize;
	private final String inventoryName;
	private Inventory gui = null;

	SettingWizard(final Player player, int inventorySize, String inventoryName, Plugin plugin) {
		this.inventorySize = inventorySize;
		this.inventoryName = inventoryName;
		this.player = player;
		new Listener() {
			{
				Bukkit.getPluginManager().registerEvents(this, plugin);
			}

			@EventHandler
			private void onInventoryClose(InventoryCloseEvent e) {
				if (e.getInventory().equals(gui)) {
					HandlerList.unregisterAll(this);
					onUnregister(gui);
				}
			}

			@EventHandler
			private void onQuit(PlayerQuitEvent e) {
				if (e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
					HandlerList.unregisterAll(this);
					onUnregister(gui);
				}
			}

			@EventHandler
			private void onInventoryClick(InventoryClickEvent e) {
				if (e.getInventory().equals(gui)) {
					onClick(e, gui);
				}
			}

		};
	}

	public void show() {
		this.gui = Bukkit.createInventory(null, inventorySize, inventoryName);
		openGUI(gui);
	}

	abstract void openGUI(Inventory gui);

	abstract void onClick(InventoryClickEvent e, Inventory gui);

	void onUnregister(final Inventory gui) {
		try {
			Configuration.getInstance().update();
		} catch (IOException | InvalidConfigurationException e1) {
			logger.log(Level.SEVERE, "콘피그를 업데이트하는 도중 오류가 발생하였습니다.");
		}
	}

}
