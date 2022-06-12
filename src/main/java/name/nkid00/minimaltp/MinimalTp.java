package name.nkid00.minimaltp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinimalTp implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("minimaltp");
	public static final int PERMISSION_LEVEL = 2;
	public static final long EXPIRATION_INTERVAL = 120000;  // milliseconds
	public static HashMap<UUID, TpRequest> TpRequests = new HashMap<>();

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(TpaTprCommand::register);
	}
}
