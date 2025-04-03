package gfx;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import maths.AABB;

public class VisualLibrary {
	protected static final String BASE_PATH = "/sprites";
	protected Map<String, String> imageFilepaths;
	protected Map<String, Map<String, BufferedImage>> groupedImages;
	protected Map<String, Map<String, SpriteSheet>> groupedSheets;
	protected Map<String, Map<String, AnimatedSprite>> groupedAnims;
	protected SpriteRequestHandler handler;
	
	public VisualLibrary() {
		imageFilepaths = new HashMap<>();
		groupedSheets = new HashMap<>();
		groupedAnims = new HashMap<>();
		groupedImages = new HashMap<>();
		handler = new SpriteRequestHandler();
		loadImageFilepaths();
	}
	
	
	protected void loadImageFilepaths(){
		Queue<String> folders = new LinkedList<>();
		folders.add(BASE_PATH);
		
		while (!folders.isEmpty()) {
			String directory = folders.poll();
			File currFile = new File(VisualLibrary.class
					.getResource(directory).getFile());
			File[] files = currFile.listFiles();
			
			for (File file: files) {
				String path = directory + "/" + file.getName();
				if (file.isFile()) {
					String mime = null;
					try {
						mime = Files.probeContentType(file.toPath());
					} catch (IOException e) {
						e.printStackTrace();}
					if (mime != null && mime.startsWith("image")) {
						String type = mime.substring(mime.lastIndexOf("/")).replaceFirst("/","");
						String name = file.getName();
						int lastIndex = name.lastIndexOf(".");
						if ((lastIndex > 0)); {
							String ext = name.substring(lastIndex).replaceFirst(".","");
							if (ext.compareTo(type) == 0) {
								name = name.substring(0, name.lastIndexOf("."));}
						}
						
						addImageFilepath(name, path);
						
					}
				}
				else if (file.isDirectory()) {
					folders.add(path);
				}
			}
		}
	}
	
	protected void addImageFilepath(String name, String filepath) {
		if (imageFilepaths.containsKey(name)) {
			throw new IllegalArgumentException("Image name \""+name+"\" already exists in database."
					+ " Avoid duplicate names on all images inside the package.");
		}
		
		imageFilepaths.putIfAbsent(name, filepath);
	}
	
	
	
	public SpriteRequestHandler getHandler() {
		return handler;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Image creation methods
	
	protected BufferedImage getImageFromFile(String imageFileName) {
		String filepath = imageFilepaths.get(imageFileName);
		if (filepath != null) {
			BufferedImage ref = GraphicsUtils.loadImage(filepath);
			return ref;
		}
		return null;
	}
	
	protected BufferedImage getImageRegionFromFile(String imageFileName, 
			int x, int y, int width, int height) {
		String filepath = imageFilepaths.get(imageFileName);
		if (filepath != null) {
			BufferedImage ref = GraphicsUtils.loadImage(filepath);
			BufferedImage region = ref.getSubimage(x, y, width, height);
			return region;
		}
		return null;
	}
	
	
	
	
	
	
	
	
	// Image storage methods
	
	protected void addImage(String groupName, String imageName, BufferedImage image) {
		try {
			Map<String, BufferedImage> group = groupedImages.get(groupName);
			if (group != null && group.containsKey(imageName)) 
				throw new IllegalArgumentException("Image can't be added to group because ImageName "+imageName+" is already used.");
			else
				if (group == null)
					addImageGroup(groupName);
				group.putIfAbsent(imageName, image);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	protected void replaceImage(String groupName, String targetImageName, BufferedImage image) {
		try {
			Map<String, BufferedImage> group = groupedImages.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Image GroupName "+groupName+" doesn't exist.");
			else if (!group.containsKey(targetImageName)) 
				throw new IllegalArgumentException("ImageName "+targetImageName+" doesn't exist. Consider adding the image instead.");
			else
				group.replace(targetImageName, image);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	protected BufferedImage removeImage(String groupName, String imageName) { // do deferred removal instead?
		try {
			Map<String, BufferedImage> group = groupedImages.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Image GroupName "+groupName+" doesn't exist.");
			else if (!group.containsKey(imageName)) 
				throw new IllegalArgumentException("ImageName "+imageName+" doesn't exist.");
			else
				return group.remove(imageName);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected BufferedImage getImage(String groupName, String imageName) {
		try {
			Map<String, BufferedImage> group = groupedImages.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Image GroupName "+groupName+" not found.");
			else if (!group.containsKey(imageName)) 
				throw new IllegalArgumentException("ImageName "+imageName+" not found.");
			else
				return group.get(imageName);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean containsImage(String groupName, String imageName) {
		if (groupedImages.containsKey(groupName)) {
			return groupedImages.get(groupName).containsKey(imageName);
		}
		return false;
	}
	
	protected BufferedImage[] getAllImages(String groupName) {
		if (!groupedImages.containsKey(groupName)) {
			return null;
		}
		return (BufferedImage[]) groupedImages.get(groupName).values().toArray();
	}
	
	
	
	
	
	
	
	
	
	// ImageGroup methods
	
	protected void addImageGroup(String groupName) {
		if (!groupedImages.containsKey(groupName)) {
			groupedImages.put(groupName, new HashMap<String, BufferedImage>(1, 0.75f));
		}
	}
	
	protected void removeImageGroup(String groupName) {
		if (groupedImages.containsKey(groupName)) {
			groupedImages.remove(groupName).clear();
		}
	}
	
	protected boolean containsImageGroup(String groupName) {
		return groupedImages.containsKey(groupName);
	}
	
	protected int imageGroupSize(String groupName) {
		if (!groupedImages.containsKey(groupName)) {
			return -1;
		}
		return groupedImages.get(groupName).size();
	}
	
	
	protected String[] getAllImageGroupNames() {
		return (String[]) groupedImages.keySet().toArray();
	}
	
	protected String[] getAllImageNames(String groupName) {
		if (!groupedImages.containsKey(groupName)) {
			return null;
		}
		return (String[]) groupedImages.get(groupName).keySet().toArray();
	}
	
	
	
	
	
	
	
	
	
	
	// Sprite creation methods
	
	protected SpriteSheet createSpriteSheetFromFile(String imageFileName, AABB[] frameDimensions) {
		String filepath = imageFilepaths.get(imageFileName);
		SpriteSheet sheet = null;
		if (filepath != null) {
			BufferedImage ref = GraphicsUtils.loadImage(filepath);
			BufferedImage[] images = new BufferedImage[frameDimensions.length];
			
			for (int i = 0; i < frameDimensions.length; i++) {
				AABB rect = Objects.requireNonNull(frameDimensions[i]);
				images[i] = (ref.getSubimage(rect.intMinX(),
						rect.intMinY(),
						rect.intWidth(), 
						rect.intHeight()));
			}
			
			sheet = new SpriteSheet((BufferedImage[]) images);
		}
		return sheet;
	}
	
	
	
	
	
	
	
	
	// Spritesheet storage methods

	protected void addSpriteSheet(String groupName, String sheetName, SpriteSheet sprite) {
		try {
			Map<String, SpriteSheet> group = groupedSheets.get(groupName);
			if (group != null && group.containsKey(sheetName)) 
				throw new IllegalArgumentException("Spritesheet can't be added to group because Spritesheet Name "+sheetName+" is already used.");
			else
				if (group == null)
					addSpriteSheetGroup(groupName);
				group.putIfAbsent(sheetName, sprite);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	protected void replaceSpriteSheet(String groupName, String targetSheetName, SpriteSheet sprite) {
		try {
			Map<String, SpriteSheet> group = groupedSheets.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Spritesheet GroupName "+groupName+" doesn't exist.");
			else if (!group.containsKey(targetSheetName)) 
				throw new IllegalArgumentException("Spritesheet Name "+targetSheetName+" doesn't exist. Consider adding the sprite instead.");
			else
				group.replace(targetSheetName, sprite);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	protected SpriteSheet removeSpriteSheet(String groupName, String sheetName) { // do deferred removal instead?
		try {
			Map<String, SpriteSheet> group = groupedSheets.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Spritesheet GroupName "+groupName+" doesn't exist.");
			else if (!group.containsKey(sheetName)) 
				throw new IllegalArgumentException("Spritesheet Name "+sheetName+" doesn't exist.");
			else
				return group.remove(sheetName);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	protected SpriteSheet getSpriteSheet(String groupName, String spriteName) {
		try {
			Map<String, SpriteSheet> group = groupedSheets.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Spritesheet GroupName "+groupName+" not found.");
			else if (!group.containsKey(spriteName)) 
				throw new IllegalArgumentException("Spritesheet Name "+spriteName+" not found.");
			else
				return group.get(spriteName);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean containsSpriteSheet(String groupName, String spriteName) {
		if (groupedSheets.containsKey(groupName)) {
			return groupedSheets.get(groupName).containsKey(spriteName);
		}
		return false;
	}
	
	protected SpriteSheet[] getAllSpriteSheets(String groupName) {
		if (!groupedSheets.containsKey(groupName)) {
			return null;
		}
		return (SpriteSheet[]) groupedSheets.get(groupName).values().toArray();
	}
	
	
	
	
	
	
	
	
	// SpritesheetGroup methods
	
	protected void addSpriteSheetGroup(String groupName) {
		if (!groupedSheets.containsKey(groupName)) {
			groupedSheets.put(groupName, new HashMap<String, SpriteSheet>(1, 0.75f));
		}
	}
	
	protected void removeSpriteSheetGroup(String groupName) {
		if (groupedSheets.containsKey(groupName)) {
			groupedSheets.remove(groupName).clear();
		}
	}
	
	protected boolean containsSpriteSheetGroup(String groupName) {
		return groupedSheets.containsKey(groupName);
	}
	
	protected int spriteSheetGroupSize(String groupName) {
		if (!groupedSheets.containsKey(groupName)) {
			return -1;
		}
		return groupedSheets.get(groupName).size();
	}
	
	protected String[] getAllSpriteSheetGroupNames() {
		return (String[]) groupedSheets.keySet().toArray();
	}
	
	protected String[] getAllSpriteSheetNames(String groupName) {
		if (!groupedSheets.containsKey(groupName)) {
			return null;
		}
		return (String[]) groupedSheets.get(groupName).keySet().toArray();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//AnimatedSprite creation
	
	
	protected AnimatedSprite createAnimatedSpriteFromFile(String imageFileName, AABB[] frameDimensions, float[] frameDurations) {
		String filepath = imageFilepaths.get(imageFileName);
		AnimatedSprite anim = null;
		if (filepath != null) {
			BufferedImage ref = GraphicsUtils.loadImage(filepath);
			BufferedImage[] images = new BufferedImage[frameDimensions.length];
			
			for (int i = 0; i < frameDimensions.length; i++) {
				AABB rect = Objects.requireNonNull(frameDimensions[i]);
				images[i] = (ref.getSubimage(rect.intMinX(),
						rect.intMinY(),
						rect.intWidth(), 
						rect.intHeight()));
			}
			
			anim = new AnimatedSprite((BufferedImage[]) images, frameDurations);
		}
		return anim;
	}
	
	
	protected AnimatedSprite createAnimatedSpriteFromFile(String imageFileName, AABB[] frameDimensions, float totalDuration) {
		float[] frameDuration = new float[frameDimensions.length];
		Arrays.fill(frameDuration, totalDuration/((float) frameDimensions.length));
		
		return createAnimatedSpriteFromFile(imageFileName, frameDimensions, frameDuration);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// AnimatedSprite storage methods

	protected void addAnimation(String groupName, String animName, AnimatedSprite animation) {
		try {
			Map<String, AnimatedSprite> group = groupedAnims.get(groupName);
			if (group != null && group.containsKey(animName)) 
				throw new IllegalArgumentException("Animation can't be added to group because Animation Name "+animName+" is already used.");
			else
				if (group == null)
					addAnimationGroup(groupName);
				group.putIfAbsent(animName, animation);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	protected void replaceAnimation(String groupName, String targetAnimName, AnimatedSprite animation) {
		try {
			Map<String, AnimatedSprite> group = groupedAnims.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Animation GroupName "+groupName+" doesn't exist.");
			else if (!group.containsKey(targetAnimName)) 
				throw new IllegalArgumentException("Animation Name "+targetAnimName+" doesn't exist. Consider adding it instead.");
			else
				group.replace(targetAnimName, animation);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	protected AnimatedSprite removeAnimation(String groupName, String animName) { // do deferred removal instead?
		try {
			Map<String, AnimatedSprite> group = groupedAnims.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Animation GroupName "+groupName+" doesn't exist.");
			else if (!group.containsKey(animName)) 
				throw new IllegalArgumentException("Animation Name "+animName+" doesn't exist.");
			else
				return group.remove(animName);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	protected AnimatedSprite getAnimation(String groupName, String animName) {
		try {
			Map<String, AnimatedSprite> group = groupedAnims.get(groupName);
			if (group == null)
				throw new IllegalArgumentException("Animation GroupName "+groupName+" not found.");
			else if (!group.containsKey(animName)) 
				throw new IllegalArgumentException("Animation Name "+animName+" not found.");
			else
				return group.get(animName);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean containsAnimation(String groupName, String animName) {
		if (groupedAnims.containsKey(groupName)) {
			return groupedAnims.get(groupName).containsKey(animName);
		}
		return false;
	}
	
	protected AnimatedSprite[] getAllAnimations(String groupName) {
		if (!groupedAnims.containsKey(groupName)) {
			return null;
		}
		return (AnimatedSprite[]) groupedAnims.get(groupName).values().toArray();
	}
	
	
	
	
	
	
	
	
	// AnimationGroup methods
	
	protected void addAnimationGroup(String groupName) {
		if (!groupedAnims.containsKey(groupName)) {
			groupedAnims.put(groupName, new HashMap<String, AnimatedSprite>(1, 0.75f));
		}
	}
	
	protected void removeAnimationGroup(String groupName) {
		if (groupedAnims.containsKey(groupName)) {
			groupedAnims.remove(groupName).clear();
		}
	}
	
	protected boolean containsAnimationGroup(String groupName) {
		return groupedAnims.containsKey(groupName);
	}
	
	protected int animationGroupSize(String groupName) {
		if (!groupedAnims.containsKey(groupName)) {
			return -1;
		}
		return groupedAnims.get(groupName).size();
	}
	
	protected String[] getAllAnimationGroupNames() {
		return (String[]) groupedAnims.keySet().toArray();
	}
	
	protected String[] getAllAnimationNames(String groupName) {
		if (!groupedAnims.containsKey(groupName)) {
			return null;
		}
		return (String[]) groupedAnims.get(groupName).keySet().toArray();
	}




	
	
	
	
	
	
		
		
		
		
		
	
	
	
	
	
	
	
	
	
	
	
	public class SpriteRequestHandler {
		
		protected SpriteRequestHandler() {}
		
		
		// Image creation methods
		
		public BufferedImage getImageFromFile(String imageFileName) {
			return VisualLibrary.this.getImageFromFile(imageFileName);
		}
		
		public BufferedImage getImageRegionFromFile(String imageFileName, 
				int x, int y, int width, int height) {
			return VisualLibrary.this.getImageRegionFromFile(imageFileName, 
					x, y, width, height);
		}
		
		
		
		
		
		// Image storage methods
		
		public void addImage(String groupName, String imageName, BufferedImage image) {
			VisualLibrary.this.addImage(groupName, imageName, image);
		}
		
		public void replaceImage(String groupName, String targetImageName, BufferedImage image) {
			VisualLibrary.this.replaceImage(groupName, targetImageName, image);
		}
		
		public BufferedImage removeImage(String groupName, String imageName) { // do deferred removal instead?
			return VisualLibrary.this.removeImage(groupName, imageName);
		}
		
		public BufferedImage getImage(String groupName, String imageName) {
			return VisualLibrary.this.getImage(groupName, imageName);
		}
		
		public boolean containsImage(String groupName, String imageName) {
			return VisualLibrary.this.containsImage(groupName, imageName);
		}
		
		public BufferedImage[] getAllImages(String groupName) {
			return VisualLibrary.this.getAllImages(groupName);
		}
		
		
		
		
		
		
		// ImageGroup methods
		
		public void addImageGroup(String groupName) {
			VisualLibrary.this.addImageGroup(groupName);
		}
		
		public void removeImageGroup(String groupName) {
			VisualLibrary.this.removeImageGroup(groupName);
		}
		
		public boolean containsImageGroup(String groupName) {
			return VisualLibrary.this.containsImageGroup(groupName);
		}
		
		public int imageGroupSize(String groupName) {
			return VisualLibrary.this.imageGroupSize(groupName);
		}
		
		
		public String[] getAllImageGroupNames() {
			return VisualLibrary.this.getAllImageGroupNames();
		}
		
		public String[] getAllImageNames(String groupName) {
			return VisualLibrary.this.getAllImageNames(groupName);
		}
		
		

		
		
		
		
		// Sprite creation methods
		
		public SpriteSheet createSpriteSheetFromFile(String imageFileName, AABB[] frameDimensions) {
			return VisualLibrary.this.createSpriteSheetFromFile(imageFileName, frameDimensions);
		}
		

		
		
		
		
		
		
		// SpriteSheet storage methods

		public void addSpriteSheet(String groupName, String sheetName, SpriteSheet sprite) {
			VisualLibrary.this.addSpriteSheet(groupName, sheetName, sprite);
		}
		
		public void replaceSpriteSheet(String groupName, String targetSheetName, SpriteSheet sprite) {
			VisualLibrary.this.replaceSpriteSheet(groupName, targetSheetName, sprite);
		}
		
		public SpriteSheet removeSpriteSheet(String groupName, String sheetName) { // do deferred removal instead?
			return VisualLibrary.this.removeSpriteSheet(groupName, sheetName);
		}
		
		
		public SpriteSheet getSpriteSheet(String groupName, String sheetName) {
			return VisualLibrary.this.getSpriteSheet(groupName, sheetName);
		}
		
		public boolean containsSpriteSheet(String groupName, String sheetName) {
			return VisualLibrary.this.containsSpriteSheet(groupName, sheetName);
		}
		
		public SpriteSheet[] getAllSpriteSheets(String groupName) {
			return VisualLibrary.this.getAllSpriteSheets(groupName);
		}
		
		
		
		
		
		
		// SpriteSheetGroup methods
		
		public void addSpriteSheetGroup(String groupName) {
			VisualLibrary.this.addSpriteSheetGroup(groupName);
		}
		
		public void removeSpriteSheetGroup(String groupName) {
			VisualLibrary.this.removeSpriteSheetGroup(groupName);
		}
		
		public boolean containsSpriteSheetGroup(String groupName) {
			return VisualLibrary.this.containsSpriteSheetGroup(groupName);
		}
		
		public int spriteSheetGroupSize(String groupName) {
			return VisualLibrary.this.spriteSheetGroupSize(groupName);
		}
		
		public String[] getAllSpriteSheetGroupNames() {
			return VisualLibrary.this.getAllSpriteSheetGroupNames();
		}
		
		public String[] getAllSpriteSheetNames(String groupName) {
			return VisualLibrary.this.getAllSpriteSheetNames(groupName);
		}
		
		
		
		
		
		
		
		
		//AnimatedSprite creation methods
		
		public AnimatedSprite createAnimatedSpriteFromFile(String imageFileName, AABB[] frameDimensions, float[] frameDurations) {
			return VisualLibrary.this.createAnimatedSpriteFromFile(imageFileName, frameDimensions, frameDurations);
		}
		
		public AnimatedSprite createAnimatedSpriteFromFile(String imageFileName, AABB[] frameDimensions, float totalDuration) {
			return VisualLibrary.this.createAnimatedSpriteFromFile(imageFileName, frameDimensions, totalDuration);
		}
		
		
		
		
		
		
		
		
		
		
		
		// AnimatedSprite storage methods

		public void addAnimation(String groupName, String animName, AnimatedSprite animation) {
			VisualLibrary.this.addAnimation(groupName, animName, animation);
		}
		
		public void replaceAnimation(String groupName, String targetAnimName, AnimatedSprite animation) {
			VisualLibrary.this.replaceAnimation(groupName, targetAnimName, animation);
		}
		
		public AnimatedSprite removeAnimation(String groupName, String animName) { // do deferred removal instead?
			return VisualLibrary.this.removeAnimation(groupName, animName);
		}
		
		
		public AnimatedSprite getAnimation(String groupName, String animName) {
			return VisualLibrary.this.getAnimation(groupName, animName);
		}
		
		public boolean containsAnimation(String groupName, String animName) {
			return VisualLibrary.this.containsAnimation(groupName, animName);
		}
		
		public AnimatedSprite[] getAllAnimations(String groupName) {
			return VisualLibrary.this.getAllAnimations(groupName);
		}
		
		
		
		
		
		
		
		
		// AnimationGroup methods
		
		public void addAnimationGroup(String groupName) {
			VisualLibrary.this.addAnimationGroup(groupName);
		}
		
		public void removeAnimationGroup(String groupName) {
			VisualLibrary.this.removeAnimationGroup(groupName);
		}
		
		public boolean containsAnimationGroup(String groupName) {
			return VisualLibrary.this.containsAnimationGroup(groupName);
		}
		
		public int animationGroupSize(String groupName) {
			return VisualLibrary.this.animationGroupSize(groupName);
		}
		
		public String[] getAllAnimationGroupNames() {
			return VisualLibrary.this.getAllAnimationGroupNames();
		}
		
		public String[] getAllAnimationNames(String groupName) {
			return VisualLibrary.this.getAllAnimationNames(groupName);
		}
		
	}
	
}
