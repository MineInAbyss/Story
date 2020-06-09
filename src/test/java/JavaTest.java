import io.github.paul1365972.metay.datastore.DataKey;
import io.github.paul1365972.metay.datastore.endpoints.MemoryDataStore;
import kotlin.Unit;
import org.bukkit.Location;
import org.junit.Test;

//TODO
public class JavaTest {
	
	@Test
	public void test() {
		Location loc = new Location(null, 0.0, 0.0, 0.0);
		
		MemoryDataStore<Location> blockStore = new MemoryDataStore<>();
		
		DataKey<MagicData> magicKey = new DataKey<>(null, "magic", (o, oos) -> {
			oos.writeInt(o.a);
			oos.writeUTF(o.b);
		}, (ois) -> new MagicData(ois.readInt(), ois.readUTF()));
		
		blockStore.compute(magicKey, loc, (value) -> {
			if (value == null)
				value = new MagicData(0, "lorem");
			value.a += 3;
			return value;
		});
		
		blockStore.update(magicKey, loc, (access, value) -> {
			if (value == null)
				value = new MagicData(0, "lorem");
			value.a += 3;
			access.set(value);
			return Unit.INSTANCE;
		});
	}
	
	static class MagicData {
		public int a;
		public String b;
		
		public MagicData(int a, String b) {
			this.a = a;
			this.b = b;
		}
	}
}