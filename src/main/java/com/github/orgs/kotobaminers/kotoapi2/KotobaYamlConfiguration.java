package com.github.orgs.kotobaminers.kotoapi2;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class KotobaYamlConfiguration {


	public static final String EXTENSION = ".yml";


	public abstract String getFileName();
	public abstract void setFileName(String name);
	public abstract File getDirectory();
	public abstract void saveYaml();
	public abstract KotobaYamlConfiguration importYaml(YamlConfiguration config);
	public abstract void deleteYaml();


	public boolean rename(String newName) {
		Optional<File> oldFile = findFile();
		File newFile = new File(getDirectory() + "/" + newName + EXTENSION);
		boolean success = false;
		if (!newFile.exists() && oldFile.isPresent()) {
			success = oldFile.get().renameTo(newFile);
			if(success) {
				findFile().ifPresent(file -> file.delete());
				setFileName(newName);
			}
		}
		return success;
	}


	private Optional<File> findFile() {
		File file = new File(getDirectory() + "/" + getFileName() + EXTENSION);
		if(file.exists()) {
			return Optional.of(file);
		}
		return Optional.empty();
	}


	public File getFileEvenCreate() {
		File file = new File(getDirectory() + "/" + getFileName() + EXTENSION);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public YamlConfiguration getConfiguration() {
		return YamlConfiguration.loadConfiguration(getFileEvenCreate());
	}


}

