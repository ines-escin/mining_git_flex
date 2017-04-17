package repositorySearch

import groovy.util.logging.Slf4j
import util.ConstantData
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Slf4j
class FileHandler {

    private static void registryDownloadProblem(String text) {
        FileWriter writer = new FileWriter(ConstantData.DOWNLOAD_PROBLEMS_FILE, true)
        writer.write(text+"\n")
        writer.close()
    }

    /**
     * Downloads a zip file.
     *
     * @param zipUrl url for the download.
     * @param zipPath path to save downloaded zip file.
     * @throws Exception if there's an error during downloading.
     */
    static void downloadZipFile(String zipUrl, String zipPath) throws Exception {
        zipUrl = zipUrl.replaceAll(" ", "")
        log.info "Downloading zipfile: ${zipUrl}"
        try {
            def inputStream = new BufferedInputStream(new URL(zipUrl).openStream())
            FileOutputStream fos = new FileOutputStream(zipPath)
            BufferedOutputStream bout = new BufferedOutputStream(fos, 8192)
            byte[] data = new byte[8192]
            int x
            while ((x = inputStream.read(data, 0, 8192)) >= 0) {
                bout.write(data, 0, x)
            }
            bout.close()
            inputStream.close()
            log.info "Done downloading!"
        } catch (IOException e) {
            registryDownloadProblem(zipUrl)
            throw new Exception("Problem during download: " + e.getMessage())
        }
    }

    /**
     * Unzips repository's zip file and saves it at "unzipped" folder.
     * @param zipPath path of zip file.
     * @param outputFolder place to save zip file content.
     * @throws Exception if there's an error during unzipping.
     */
    static void unzip(String zipPath, String outputFolder) throws Exception {
        byte[] buffer = new byte[1024]
        log.info "Unzipping zipfile: $zipPath"

        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath)) //get the zip file content
            ZipEntry ze = zis.getNextEntry() //get the zipped file list entry

            while (ze != null) {
                String fileName = ze.getName()
                File newFile = new File(outputFolder + File.separator + fileName)
                if (ze.isDirectory()) {
                    new File(newFile.getParent()).mkdirs()
                } else {
                    FileOutputStream fos
                    new File(newFile.getParent()).mkdirs()
                    fos = new FileOutputStream(newFile)
                    int len
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len)
                    }
                    fos.close()
                }
                ze = zis.getNextEntry()
            }
            zis.closeEntry()
            zis.close()
            log.info "Done unzipping!"

        } catch (IOException e) {
            deleteFolder(outputFolder)
            deleteZipFile(zipPath)
            throw new Exception("Problem during unzipping: " + e.getMessage())
        }
    }

    static boolean hasFileType(String type, String folder) {
        boolean result = false
        File dir = new File(folder)
        File[] files = dir.listFiles()

        if (files == null) return false

        for (File f : files) {
            if (result) return true
            else if (f.isDirectory()) result = hasFileType(type, f.getAbsolutePath())
            else if (f.getName().endsWith(type)) {
                result = true
                break
            }
        }
        result
    }

    static File retrieveFile(String name, String folder) {
        File result = null
        File dir = new File(folder)
        File[] files = dir.listFiles()

        if (files == null) return null

        for (File f : files) {
            if (result) return result
            else if (f.isDirectory()) result = retrieveFile(name, f.getAbsolutePath())
            else if (f.getName().endsWith(name)) {
                result = f
                break
            }
        }
        result
    }

    /**
     * Deletes a folder and its files.
     *
     * @param folder folder's path.
     */
    static void deleteFolder(String folder) {
        File dir = new File(folder)
        File[] files = dir.listFiles()
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f.getAbsolutePath())
                } else f.delete()
            }
        }
        dir.delete()
    }

    /**
     * Deletes a zip file if it does exist.
     *
     * @param path zip file path.
     */
    static void deleteZipFile(String path) {
        File file = new File(path)
        file.delete()
    }

}
