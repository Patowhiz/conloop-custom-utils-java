package conloop;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author PatoWhiz 20/09/2019 10:16 AM
 */
public class CustomFileUtil {

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

    public static void copyFile(File sourceFile, File destinationFile) throws IOException {
        copyFolderORFile(sourceFile, destinationFile);
    }

    public static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        copyFolderORFile(sourceFolder, destinationFolder);
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
                LoggingUtil.e(CustomFileUtil.class, ex.getMessage());
            } catch (Exception ex) {
                LoggingUtil.e(CustomFileUtil.class, ex.getMessage());
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

    public static boolean deleteFile(String filePathAndName) {
        File file = new File(filePathAndName);
        if (file.exists()) {
            deleteFolderOrFile(file);
        }
        //todo
        return true;
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

    public static void openFileInAnotherApplication(String filePathAndName) throws IOException {
        openFileInAnotherApplication(new File(filePathAndName));
    }

    public static void openFileInAnotherApplication(File file) throws IOException {
        Desktop.getDesktop().open(file);
    }

    public static boolean zipFile(String dirToZipName, String zipFileName) {
        return zipFile(new File(dirToZipName), zipFileName);
    }

    public static boolean zipFile(File dirToZip, String zipFileName) {
        boolean bZipped = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(zipFileName);
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                zipDirectoryFiles(dirToZip, dirToZip.getName(), zipOut);
            }
            bZipped = true;
        } catch (FileNotFoundException ex) {
            LoggingUtil.e(CustomFileUtil.class, ex);
            bZipped = false;
        } catch (IOException ex) {
            LoggingUtil.e(CustomFileUtil.class, ex);
            bZipped = false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                LoggingUtil.e(CustomFileUtil.class, ex);
            }
        }
        return bZipped;
    }

    private static void zipDirectoryFiles(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            //for directories, do the following and return

            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipDirectoryFiles(childFile, fileName + "/" + childFile.getName(), zipOut);
            }//end for loop
        } else {

            //for files do the the following
            try (FileInputStream fis = new FileInputStream(fileToZip)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }//end try

        }//end outer if

    }

    //todo. not yet tested. comment made on 02/04/2021
    public void unzipIt(String fileToUnZip, String destDirectory) throws IOException {
        //String fileToUnZip = "src/main/resources/unzipTest/compressed.zip";
        File destDir = new File(destDirectory);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileToUnZip))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFileToUnzip(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    //fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    //write file content
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }//end try
                }//end if
                zipEntry = zis.getNextEntry();
            }//end while
            zis.closeEntry();
        }
    }

    private static File newFileToUnzip(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static String readTextFile(String strfileNameAndPath) {
        String strFileContents = null;
        try {
            File conFile = new File(strfileNameAndPath);
            //if not exist. Return null
            if (conFile.exists()) {
                try (Scanner input = new Scanner(conFile)) {
                    if (input.hasNext()) {
                        strFileContents = input.next();
                    }
                }
            }//end if

        } catch (FileNotFoundException ex) {
            System.err.println("Error file not found. Terminating : " + ex.getMessage());
        } catch (NoSuchElementException elementException) {
            System.err.println("File improperly formed. Terminating : " + elementException.getMessage());
        } catch (IllegalStateException stateException) {
            System.err.println("Error reading from file. Terminating : " + stateException.getMessage());
        } catch (Exception ioex) {
            System.err.println("IO Exception. Terminating : " + ioex.getMessage());
        }

        return strFileContents;
    }//end method

}//end class
