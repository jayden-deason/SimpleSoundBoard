import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Profile {
	
	String name;
	static Properties properties = new Properties();
	Profile(String profName)
	{
		name = profName;
	}
	
	static public void saveProfiles(ArrayList<Profile> arr)
	{
		arr=Profile.sort(arr);
		for(int i=0; i<arr.size(); i++)
		{
		properties.setProperty("profiles" + (i+1), arr.get(i).getName());
		properties.setProperty("maxNum","" + (arr.size()));
  	    try {
	  	     properties.store(new FileOutputStream("userConfig" + File.separator + "name.properties"), null);
  	    } catch (IOException l) {
  	    }
  	  }
	}
	
	static public void deleteProfile(int i)
	{
		try {
			FileOutputStream file = new FileOutputStream("userConfig" + File.separator + "name.properties");
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getName()
	{
		return name;
	}

	static public ArrayList<Profile> loadProfiles()
	{
		ArrayList<Profile> prof = new ArrayList<Profile>();
		try {
		      properties.load(new FileInputStream("userConfig" + File.separator +"name.properties"));
		     if(properties.getProperty("maxNum") !=null) {
		      int maxVal=Integer.parseInt(properties.getProperty("maxNum"));
				for(int numProf=1; numProf<=maxVal; numProf++)
				{
					String profName = properties.getProperty("profiles" + numProf);
					prof.add(new Profile(profName));
				}
		     }
	    }catch (IOException l) {
		      System.err.println("Ooops!");
		}
		return prof;
	}
	
	static public ArrayList<Profile> sort(ArrayList<Profile> arr)
	{
		boolean added=false;
		ArrayList<Profile> profiles= new ArrayList<Profile>();
		for(Profile prof: arr)
		{
			if(profiles.isEmpty())
			{
				profiles.add(prof);
				added=true;
			}
			for(int i=0;i<profiles.size();i++)
			{
				if(profiles.get(i).getName().compareTo(prof.getName())<0)
				{
					profiles.add(i,prof);
					System.out.print(profiles);
					added=true;
					break;
				}
			}
			if(!added)
			{
				profiles.add(prof);
			}
			added=false;
		}
		return profiles;
	}
	
	public String toString()
	{
		return this.getName();
	}
}
