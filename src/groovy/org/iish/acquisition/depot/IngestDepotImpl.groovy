package org.iish.acquisition.depot

import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.*
import org.apache.log4j.Logger

/**
 * Provides actions on the ingest depot using FTP.
 */
class IngestDepotImpl implements IngestDepot {

    Logger log = Logger.getLogger('org.iish.acquisition.depot.IngestDepotImpl')
    private FTPClient client
    private String path = '/'

    /**
     * Constructor call for FTP access to the ingest depot.
     * @param host Ftp server fqdn
     * @param port Ftp port
     * @param username Username
     * @param password Credentials
     * @param secure Connect over a secure channel or otherwise plain
     * @param enterLocalPassiveMode Use passive connections
     */
    IngestDepotImpl(String host, int port, String username, String password, boolean secure, boolean enterLocalPassiveMode) {
        client = (secure) ? new FTPSClient() : new FTPClient()

        if (log.isDebugEnabled()) {
            client.addProtocolCommandListener(new PrintCommandListener(new PrintWriterImp(System.out)))
        }

        client.connect(host, port)
        if (FTPReply.isNegativePermanent(client.replyCode)) {
            // log or throw exception
            throw new Exception('Connection failed ' + client.getReplyString())
        }

        if (!client.login(username, password)) {
            throw new Exception('Login failed.')
        }

        if (secure) { // encrypt the data channel
            client.execPBSZ(0)
            client.execPROT('P')
        }
        client.type(FTP.BINARY_FILE_TYPE)
        if (enterLocalPassiveMode)
            client.enterLocalPassiveMode()
    }

    /**
     * Constructor call for FTP access to the ingest depot.
     * @param host Ftp server fqdn
     * @param port Ftp port
     * @param username Username
     * @param password Credentials
     * @param secure Connect over a secure channel or otherwise plain
     * @param enterLocalPassiveMode Use passive connections
     * @param path The path of the current working directory on the ingest depot.
     */
    IngestDepotImpl(String host, int port, String username, String password, boolean secure, boolean enterLocalPassiveMode, String path) {
        this(host, port, username, password, secure, enterLocalPassiveMode)
        setPath(path)
    }

    /**
     * Sets the path of the current working directory.
     * @param path The path in question.
     */
    @Override
    void setPath(String path) {
        client.changeWorkingDirectory(getValidPath(path))
        this.path = client.printWorkingDirectory()
    }

    /**
     * Returns the path of the current working directory.
     * @return The path in question.
     */
    @Override
    String getPath() {
        return path
    }

    /**
     * Returns the path of the current working directory, split per folder in the hierarchy.
     * @return The folders in the hierarchy.
     */
    @Override
    String[] getPathAsArray() {
        return path.split('/').findAll { isValidName(it) }
    }

    /**
     * Lists the folders and files in the current working directory.
     * @return The folders and files.
     */
    @Override
    List<IngestDepotFile> list() {
        return client.listFiles().findAll { FTPFile file ->
            return ((file.isDirectory() || file.isFile()) && isValidName(file.getName()))
        }.collect { FTPFile file ->
            IngestDepotFile ingestDepotFile = new IngestDepotFile()
            ingestDepotFile.path = getValidPath(path + '/' + file.getName())
            ingestDepotFile.name = file.getName()
            ingestDepotFile.size = file.getSize()
            ingestDepotFile.isDirectory = file.isDirectory()

            return ingestDepotFile
        }.sort { IngestDepotFile file1, IngestDepotFile file2 ->
            if (file1.isDirectory() && file2.isDirectory()) {
                return file1.getName().compareTo(file2.getName())
            } else {
                return file1.isDirectory() ? -1 : 1
            }
        }
    }

    /**
     * Removes the file or folder on the given path from the ingest depot.
     * @param path The file or folder to delete.
     */
    @Override
    void remove(String path) {
        path = getValidPath(path)
        FTPFile[] files = client.listFiles(path).findAll { isValidName(it.getName()) }

        files.each { FTPFile file ->
            if (file.isDirectory()) {
                String newPath = "$path/${file.getName()}"
                remove(newPath)
            } else {
                client.deleteFile(path)
            }
        }

        client.removeDirectory(path)
    }

    /**
     * Closes the connection to the ingest depot.
     */
    @Override
    void close() {
        client.logout()
        client.disconnect()
    }

    /**
     * Returns a valid version of the given path. (Without empty folder names, '.' and '..')
     * @param path The path in question.
     * @return The valid version of the given path.
     */
    private static String getValidPath(String path) {
        return '/' + path.split('/').findAll { isValidName(it) }.collect { it.trim() }.join('/')
    }

    /**
     * Returns whether the given folder name is valid. (Not empty , '.' or '..')
     * @param name The name of the folder in question.
     * @return Whether the name is valid.
     */
    private static boolean isValidName(String name) {
        return (!name.isAllWhitespace() && !name.equals('.') && !name.equals('..'))
    }

    /**
     * Prints out lines of text and applies a filter to hide user credentials
     */
    class PrintWriterImp extends PrintWriter {

        PrintWriterImp(OutputStream out) {
            super(out)
        }

        @Override
        void write(String s) {
            String line = (s.startsWith('PASS ')) ? "PASS *\n" : s
            super.write(line)
        }
    }
}
