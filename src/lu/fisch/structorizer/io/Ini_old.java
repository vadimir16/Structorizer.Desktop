/*
    Structorizer
    A little tool which you can use to create Nassi-Schneiderman Diagrams (NSD)

    Copyright (C) 2009  Bob Fisch

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.structorizer.io;

/******************************************************************************************************
 *
 *      Author:         Bob Fisch
 *
 *      Description:    This class manages entries in the INI-file
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------			----			-----------
 *      Bob Fisch       2008.05.02      First Issue
 *
 ******************************************************************************************************
 *
 *      Comment:		
 *
 ******************************************************************************************************///

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class Ini_old {

	private String ininame = "structorizer.ini";
	private String filename = "";
	private String filename2 = "";
	private File dir = null;
	private File file = null;
	private Properties p = new Properties();
	private static Ini_old ini = null;
        private boolean regularExists = false;
        private boolean alternateExists = false;

        private static boolean useAppData = false;

        public static String getDirname()
        {
            // mac
            if(System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
            {
                return System.getProperty("user.home")+"/Library/Application Support/Structorizer";
            }
            // windows
            else if (System.getProperty("os.name").toLowerCase().indexOf( "win" ) >= 0)
            {
                String appData = System.getenv("APPDATA");
                if(appData!=null)
                    if (!appData.equals(""))
                    {
                        return appData+"\\Structorizer";
                    }
                return System.getProperty("user.home") + "\\Application Data\\Structorizer";
            }
            else
                return System.getProperty("user.home")+System.getProperty("file.separator")+".unimozer";
        }

        public static Ini_old getInstance()
	{
            if (ini==null)
            {
                try
                {
                    ini = new Ini_old();
                }
                catch (FileNotFoundException ex)
                {
                    System.out.println(ex.getMessage());
                }
                catch (IOException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
            return ini;
	}



	public void loadRegular() throws FileNotFoundException, IOException
	{
            //System.out.println("Trying to load INI file: "+filename);
            //JOptionPane.showMessageDialog(null, "Loading from => "+filename);
            File f = new File(filename);
            if(f.length()==0)
            {
                System.out.println("File is empty!");
            }
            else
            {
                //p.loadFromXML(new FileInputStream(filename));
                p.load(new FileInputStream(filename));
                //System.out.println(p.toString());
            }
	}

	public void loadAlternate() throws FileNotFoundException, IOException
	{
            //System.out.println("Trying to load INI file: "+filename);
            File f = new File(filename2);
            if(f.length()==0)
            {
                System.out.println("File is empty!");
            }
            else
            {
                //p.loadFromXML(new FileInputStream(filename));
                p.load(new FileInputStream(filename2));
                //System.out.println(p.toString());
            }
	}

	public void load() throws FileNotFoundException, IOException
	{
		//if(regularExists) loadRegular();
		//if(alternateExists) loadAlternate();
                loadRegular();
	}

        public void load(String _filename) throws FileNotFoundException, IOException
	{
		File f = new File(_filename);
		if(f.length()!=0)
		{
			p.load(new FileInputStream(_filename));
		}
	}

	private void saveRegular() throws FileNotFoundException, IOException
	{
		p.store(new FileOutputStream(filename), "last updated " + new java.util.Date());
                //JOptionPane.showMessageDialog(null, "Regular saved");
	}

	private void saveAlternate() throws FileNotFoundException, IOException
	{
		p.store(new FileOutputStream(filename2), "last updated " + new java.util.Date());
                //JOptionPane.showMessageDialog(null, "Alternate saved => "+filename2);
	}

	public void save() throws FileNotFoundException, IOException
	{
 		//if(regularExists) saveRegular();
		//if(alternateExists) saveAlternate();
 		saveRegular();
	}
        
	public void save(String _filename) throws FileNotFoundException, IOException
	{
		p.store(new FileOutputStream(_filename), "last updated " + new java.util.Date());
	}
        

	public String getProperty(String _name, String _default)
	{
		if (p.getProperty(_name)==null)
		{
			return _default;
		}
		else
		{
			return p.getProperty(_name);
		}
	}
	
	public void setProperty(String _name, String _value)
	{
		p.setProperty(_name,_value);
	}
	
	public Set keySet()
	{
		return p.keySet();
	}

	private Ini_old() throws FileNotFoundException, IOException
	{
            String dirname = "";
            String dirname2 = "";

            // regular INI file
            try
            {
                dirname = System.getProperty("user.home")+System.getProperty("file.separator")+".structorizer";
                if (useAppData==true) dirname=getDirname();
                filename = dirname+System.getProperty("file.separator")+ininame;
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            // does the regular file exists?
            try
            {
                file = new File(filename);
                regularExists=file.exists();
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            // alternate INI file
            try
            {
                URL mySource = Ini.class.getProtectionDomain().getCodeSource().getLocation();
                File sourceFile = new File(mySource.getPath());
                dirname2=sourceFile.getAbsolutePath();
                filename2 = dirname2+System.getProperty("file.separator")+ininame;
                filename2 = filename2.replaceFirst("Structorizer.jar\\"+System.getProperty("file.separator"), "");
                filename2 = filename2.replaceFirst("\\"+System.getProperty("file.separator")+"Contents\\"+System.getProperty("file.separator")+"Resources\\"+System.getProperty("file.separator")+"Java", "");
                //filename2 = filename2.replaceFirst("\\\\Structorizer.app", "");
                filename2=URLDecoder.decode(filename2);
                //System.out.println(filename);
                //System.out.println(filename2);
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            // does the alternatve file exists?
            try
            {
                file = new File(filename2);
                alternateExists=file.exists();
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            //JOptionPane.showMessageDialog(null, filename+" ==> "+regularExists);
            //JOptionPane.showMessageDialog(null, filename2+" ==> "+alternateExists);


            // if ano file has been found
            if(!regularExists & !alternateExists)
            {
                // create the regular one
                try
                {
                    dir = new File(dirname);
                    file = new File(filename);

                    if(!dir.exists())
                    {
                            dir.mkdir();
                    }

                    if(!file.exists())
                    {
                            try
                            {
                                    //setProperty("dummy","dummy");
                                    saveRegular();
                            }
                            catch (Exception e)
                            {
                                    e.printStackTrace();
                                    System.out.println(e.getMessage());
                            }
                    }

                    regularExists=true;
                }
                catch(Error e)
                {
                    System.out.println(e.getMessage());
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
            else if(alternateExists)
            {
                filename=filename2;
                alternateExists=false;
            }

            // load the file once!
            //loadRegular();

            /*
            // alternate INI file
            try
            {
                URL mySource = Ini.class.getProtectionDomain().getCodeSource().getLocation();
                File sourceFile = new File(mySource.getPath());
                dirname2=sourceFile.getAbsolutePath();
                filename2 = dirname2+System.getProperty("file.separator")+ininame;
                filename2 = filename2.replaceFirst("Structorizer.jar/", "");
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            // regular INI file
            try
            {
                dirname = System.getProperty("user.home")+System.getProperty("file.separator")+".structorizer";
                filename = dirname+System.getProperty("file.separator")+ininame;
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            // regular INI file
            try
            {
                dir = new File(dirname);
                file = new File(filename);

                if(!dir.exists())
		{
			dir.mkdir();
		}
		
		if(!file.exists())
		{
			try
			{	
				//setProperty("dummy","dummy");
				saveRegular();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            // alternate INI file
            try
            {
                dir2 = new File(dirname2);
                file2 = new File(filename2);

		if(!file2.exists())
		{
			try
			{
				//setProperty("dummy","dummy");
				saveAlternate();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
            }
            catch(Error e)
            {
                System.out.println(e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
             */
	}

    /**
     * @return the userAppData
     */
    public static boolean isUsingAppData()
    {
        return useAppData;
    }

    /**
     * @param userAppData the userAppData to set
     */
    public static void setUseAppData(boolean puseAppData)
    {
        useAppData = puseAppData;
    }

}
