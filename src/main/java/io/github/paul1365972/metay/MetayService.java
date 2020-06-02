package io.github.paul1365972.metay;

import io.github.paul1365972.metay.storage.impl.*;
import org.bukkit.entity.Item;

public interface MetayService {
	
	LocationDataStore getBlockStore();
	ChunkDataStore getChunkStore();
	UUIDDataStore getWorldStore();
	EntityDataStore getEntityStore();
	ItemDataStore getItemStore();
	
}
