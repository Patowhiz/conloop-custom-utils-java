package conloop;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author PatoWhiz 20/09/2019 10:16 AM
 */
public class FileUtil {

    public static boolean fileExist(String filePathAndName) {
        boolean exists = false;
        try {
            exists = new File(filePathAndName).exists();
        } catch (Exception ex) {
            System.err.println("Error in getting file");
        }//end try

        return exists;
    }

    public static String getfileURIString(String filePathAndName) {
        String fileUriString = "";
        try {
            File file = new File(filePathAndName);
            if (file.exists()) {
                fileUriString = file.toURI().toString();
            }
        } catch (Exception ex) {
            System.err.println("Error in getting file");
        }//end try

        return fileUriString;
    }

    public static void copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        copyFolderORFile(sourceFilePath, destinationFilePath);
    }

    public static void copyFolder(String sourceFolderPath, String destinationFolderPath) throws IOException {
        copyFolderORFile(sourceFolderPath, destinationFolderPath);
    }

    private static void copyFolderORFile(String sourceFolderPath, String destinationFolderPath) throws IOException {
        File destinationFolder = new File(destinationFolderPath);
        File sourceFolder = new File(sourceFolderPath);
        copyFolderORFile(sourceFolder, destinationFolder);
    }

    private static void copyFolderORFile(File src, File dest) throws IOException {

        if (src.isDirectory()) {

            if (!dest.exists()) {
                dest.mkdir();
                //System.out.println("Directory copied from " + src + " to " + dest);
            }//end inner if

            //list all the directory contents
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolderORFile(srcFile, destFile);
            }
        } else {
            java.nio.channels.FileChannel inputChannel = null;
            java.nio.channels.FileChannel outputChannel = null;
            try {
                inputChannel = new java.io.FileInputStream(src).getChannel();
                outputChannel = new java.io.FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException ex) {
                LoggingUtil.e(FileUtil.class, ex.getMessage());
            } catch (Exception ex) {
                LoggingUtil.e(FileUtil.class, ex.getMessage());
            } finally {
                if (inputChannel != null) {
                    inputChannel.close();
                }
                if (outputChannel != null) {
                    outputChannel.close();
                }
            }
        }//end outer if
    }//end method

    public static void deleteFile(String filePathAndName) {
        File file = new File(filePathAndName);
        if (file.exists()) {
            deleteFolderOrFile(file);
        }
    }

    public static void deleteFolderOrFile(File src) {
        if (src.isDirectory()) {
            //list all the directory contents
            String files[] = src.list();
            for (String file : files) {
                deleteFolderOrFile(new File(src, file));
            }
        } else {
            src.delete();
        }
    }

}//end class
