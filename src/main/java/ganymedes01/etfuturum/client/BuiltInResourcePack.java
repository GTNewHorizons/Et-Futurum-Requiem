package ganymedes01.etfuturum.client;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.awt.image.BufferedImage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// Inspired by ResourceManagerHelper in fabric-resource-loader-v0

public abstract class BuiltInResourcePack extends AbstractResourcePack {

	private static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(5);

	private final String modid;
	private final String id;
	protected boolean resourcesEnabled = true;
	protected boolean langEnabled = true;

	/**
	 * <p>Register a built-in resource pack. This is a resource pack located in the JAR at {@code "resourcepacks/<id>"}.
	 *
	 * <p>The resource pack is "invisible", it will not show up in the resource pack GUI.
	 *
	 * @param id The name of the resource pack.
	 */
	public static BuiltInResourcePack register(String id) {
		BuiltInResourcePack rp = BuiltInResourcePack.of(Loader.instance().activeModContainer().getSource(), Loader.instance().activeModContainer().getModId(), id);
		if (rp.isAllEnabled()) {
			inject(rp);
		}
		return rp;
	}

	private static BuiltInResourcePack of(File file, String modid, String id) {
		if (file.isDirectory()) {
			return new BuiltInFolderResourcePack(file, modid, id);
		} else {
			return new BuiltInFileResourcePack(file, modid, id);
		}
	}

	public BuiltInResourcePack(File file, String modid, String id) {
		super(file);
		this.modid = modid;
		this.id = id;
	}

	@Override
	public String getPackName() {
		return modid + "/" + id;
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
		return null;
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		return null;
	}

	protected String getRootPath() {
		return "resourcepacks/" + id + "/";
	}

	protected void addNamespaceIfLowerCase(Set<String> set, String ns) {
		if (!ns.equals(ns.toLowerCase())) {
			this.logNameNotLowercase(ns);
		} else {
			set.add(ns);
		}
	}

	public BuiltInResourcePack setResourcesEnabled() {
		this.resourcesEnabled = ConfigFunctions.enableNewTextures;
		this.langEnabled = ConfigFunctions.enableLangReplacements;
		return this;
	}

	private boolean isAllEnabled() {
		return resourcesEnabled && langEnabled;
	}

	private static void inject(IResourcePack resourcePack) {
		List<IResourcePack> defaultResourcePacks = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao");
		defaultResourcePacks.add(resourcePack);
		IResourceManager resMan = Minecraft.getMinecraft().getResourceManager();
		if (resMan instanceof SimpleReloadableResourceManager) {
			((SimpleReloadableResourceManager) resMan).reloadResourcePack(resourcePack);
		}
	}

	private static class BuiltInFileResourcePack extends BuiltInResourcePack {

		private final ZipFile zipFile;

		public BuiltInFileResourcePack(File file, String modid, String id) {
			super(file, modid, id);
			try {
				this.zipFile = new ZipFile(file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Set<String> getResourceDomains() {
			Set<String> domains = new HashSet<>();

			Enumeration<? extends ZipEntry> en = zipFile.entries();
			while (en.hasMoreElements()) {
				ZipEntry entry = en.nextElement();
				if (entry.getName().startsWith(getRootPath() + "assets/")) {
					List<String> nameParts = Lists.newArrayList(entryNameSplitter.split(entry.getName()));
					if (nameParts.size() > 3) {
						addNamespaceIfLowerCase(domains, nameParts.get(3));
					}
				}
			}
			return domains;
		}

		@Override
		protected InputStream getInputStreamByName(String name) throws IOException {
			return zipFile.getInputStream(zipFile.getEntry(getRootPath() + name));
		}

		@Override
		protected boolean hasResourceName(String name) {
			return resourcesEnabled && zipFile.getEntry(getRootPath() + name) != null;
		}

	}

	private static class BuiltInFolderResourcePack extends BuiltInResourcePack {

		public BuiltInFolderResourcePack(File file, String modid, String id) {
			super(file, modid, id);
		}

		@Override
		public Set<String> getResourceDomains() {
			Set<String> domains = new HashSet<>();

			File assetsDir = new File(this.resourcePackFile, getRootPath() + "assets/");
			if (assetsDir.isDirectory()) {
				File[] files = assetsDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
				for (File file : files) {
					addNamespaceIfLowerCase(domains, file.getName());
				}
			}

			return domains;
		}

		@Override
		protected InputStream getInputStreamByName(String name) throws IOException {
			if (name.endsWith("lang")) {
				List<String> langFile = Lists.newArrayList();

				//Reads the lang file, strips unneeded lines and adds it to a list to be compared against the ignore list
				String currentLine;
				File file = new File(this.resourcePackFile, getRootPath() + "/" + name);
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while ((currentLine = reader.readLine()) != null) {
					if (currentLine.startsWith("#") || currentLine.length() == 0) {
						continue;
					}
					langFile.add(currentLine.trim());
				}
				reader.close();


				List<String> ignoredKeys = Lists.newArrayList(); //Ignore these keys under certain conditions
				if (!ConfigBlocksItems.enableDyedBeds) {
					ignoredKeys.add("item.bed.name");
					ignoredKeys.add("tile.bed.name");
				}
				if (!ConfigBlocksItems.enableVanillaSigns) {
					ignoredKeys.add("item.sign.name");
					ignoredKeys.add("tile.sign.name");
				}
				if (!ConfigBlocksItems.enableVanillaDoors) {
					ignoredKeys.add("item.doorWood.name");
					ignoredKeys.add("tile.doorWood.name");
				}
				if (!ConfigBlocksItems.enableVanillaTrapdoors) {
					ignoredKeys.add("tile.trapdoor.name");
				}
				if (!ConfigBlocksItems.enableVanillaFences) {
					ignoredKeys.add("tile.fence.name");
					ignoredKeys.add("tile.fenceGate.name");
				}

				Iterator<String> iterator = langFile.listIterator();
				while (iterator.hasNext()) {
					String translation = iterator.next();
					for (String removalCheck : ignoredKeys) {
						if (translation.startsWith(removalCheck)) {
							iterator.remove();
						}
					}
				}
				//Strips ignored entries

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				for (String line : langFile) {
					baos.write((line + "\n").getBytes()); //Add a new line since otherwise the byte array output stream will not have any line breaks and everything goes into the first key
				}
				//Add the new lang file to a byte array output stream for the game to read

				byte[] bytes = baos.toByteArray();

				return new ByteArrayInputStream(bytes); //Ding fries are done
			}
			return new BufferedInputStream(Files.newInputStream(new File(this.resourcePackFile, getRootPath() + "/" + name).toPath()));
		}

		@Override
		protected boolean hasResourceName(String name) {
			if (!langEnabled && name.endsWith("lang")) return false;
			return resourcesEnabled && new File(this.resourcePackFile, getRootPath() + "/" + name).isFile();
		}

	}

}
