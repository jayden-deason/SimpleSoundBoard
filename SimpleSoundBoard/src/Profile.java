import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class Profile {
	String name;
	static Properties properties = new Properties();

	Profile(String profName) {
		this.name = profName;
	}

	public static void saveProfiles(ArrayList<Profile> arr) {
		arr = sort(arr);

		for(int i = 0; i < arr.size(); ++i) {
			properties.setProperty("profiles" + (i + 1), ((Profile)arr.get(i)).getName());
			properties.setProperty("maxNum", "" + arr.size());

			try {
				properties.store(new FileOutputStream("userConfig" + File.separator + "name.properties"), (String)null);
			} catch (IOException var3) {
			}
		}

	}

	public static void deleteProfile(int i) {
		try {
			FileOutputStream file = new FileOutputStream("userConfig" + File.separator + "name.properties");
			file.flush();
			file.close();
		} catch (IOException var2) {
			var2.printStackTrace();
		}

	}

	public String getName() {
		return this.name;
	}

	public static ArrayList<Profile> loadProfiles() {
		ArrayList<Profile> prof = new ArrayList();

		try {
			properties.load(new FileInputStream("userConfig" + File.separator + "name.properties"));
			if (properties.getProperty("maxNum") != null) {
				int maxVal = Integer.parseInt(properties.getProperty("maxNum"));

				for(int numProf = 1; numProf <= maxVal; ++numProf) {
					String profName = properties.getProperty("profiles" + numProf);
					prof.add(new Profile(profName));
				}
			}
		} catch (IOException var4) {
			System.err.println("Ooops!");
		}

		return prof;
	}

	public static ArrayList<Profile> sort(ArrayList<Profile> arr) {
		boolean added = false;
		ArrayList<Profile> profiles = new ArrayList();

		for(Iterator var4 = arr.iterator(); var4.hasNext(); added = false) {
			Profile prof = (Profile)var4.next();
			if (profiles.isEmpty()) {
				profiles.add(prof);
				added = true;
			}

			for(int i = 0; i < profiles.size(); ++i) {
				if (((Profile)profiles.get(i)).getName().compareTo(prof.getName()) < 0) {
					profiles.add(i, prof);
					System.out.print(profiles);
					added = true;
					break;
				}
			}

			if (!added) {
				profiles.add(prof);
			}
		}

		return profiles;
	}

	public String toString() {
		return this.getName();
	}
}
