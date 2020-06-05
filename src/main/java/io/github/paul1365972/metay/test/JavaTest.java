package io.github.paul1365972.metay.test;

import io.github.paul1365972.metay.storage.DataKey;
import io.github.paul1365972.metay.storage.endpoints.MemoryDataStore;
import io.github.paul1365972.metay.test.JavaTest.MagicData;
import kotlin.Unit;
import org.bukkit.Location;

//TODO
public class JavaTest {
	
	class MagicData {
		public int a;
		public String b;
		
		public MagicData(int a, String b) {
			this.a = a;
			this.b = b;
		}
	}
	
	void test() {
		Location loc = new Location(null, 0.0, 0.0, 0.0);
		
		MemoryDataStore<Location> blockStore = new MemoryDataStore<>();
		
    	DataKey<MagicData> magicKey = new DataKey<>(null, "magic", (o, oos) -> {
			oos.writeInt(o.a);
			oos.writeUTF(o.b);
		}, (ois) -> new MagicData(ois.readInt(), ois.readUTF()));
		
		blockStore.modify1(magicKey, loc, (value) -> {
			if (value == null)
				value = new MagicData(0, "lorem");
			value.a += 3;
			return value;
		});
		
		blockStore.modify2(magicKey, loc, (access) -> {
			MagicData value = access.get();
			if (value == null)
				value = new MagicData(0, "lorem");
			value.a += 3;
			access.set(value);
			return Unit.INSTANCE;
		});
		
		blockStore.modify3(magicKey, loc, (access, value) -> {
			if (value == null)
				value = new MagicData(0, "lorem");
			value.a += 3;
			access.set(value);
			return Unit.INSTANCE;
		});
	}
}
