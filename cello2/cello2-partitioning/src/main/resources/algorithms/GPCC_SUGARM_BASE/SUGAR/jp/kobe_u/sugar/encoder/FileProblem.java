package jp.kobe_u.sugar.encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;


public class FileProblem extends Problem {
    public static boolean USE_NEWIO = true;
    
    public static int SAT_BUFFER_SIZE = 4*1024;
    
    public static long MAX_SAT_SIZE = 3*1024*1024*1024L;
    
    public static String PRAGMA_DOMINANT = "d";
    
    private String satFileName;

    private FileChannel satFileChannel = null; 

    private ByteBuffer satByteBuffer = null;
    
    public FileProblem(String satFileName) throws SugarException {
        this.satFileName = satFileName;
        clear();
    }

    public void open() throws SugarException {
        if (satFileChannel != null)
            throw new SugarException("Internal error: re-opening file " + satFileName);
        try {
            if (fileSize == 0) {
                satFileChannel = (new FileOutputStream(satFileName)).getChannel();
            } else {
                satFileChannel = (new RandomAccessFile(satFileName, "rw")).getChannel();
                satFileChannel.position(fileSize);
            }
            satByteBuffer = ByteBuffer.allocateDirect(SAT_BUFFER_SIZE);
        } catch (IOException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }

    public void write(byte[] b) throws SugarException {
        if (satFileChannel == null)
            open();
        int len = b.length;
        if (satByteBuffer.position() + len > SAT_BUFFER_SIZE)
            flush();
        int pos = 0;
        while (len > SAT_BUFFER_SIZE) {
            satByteBuffer.put(b, pos, SAT_BUFFER_SIZE);
            flush();
            pos += SAT_BUFFER_SIZE;
            len -= SAT_BUFFER_SIZE;
        }
        satByteBuffer.put(b, pos, len);
        fileSize += b.length;
        /*if (fileSize >= MAX_SAT_SIZE) {
            (new File(satFileName)).delete();
            throw new SugarException("Encoding is interrupted because file size becomes too large (" + fileSize + " bytes)");
        }*/
    }
    
    public void write(String s) throws SugarException {
        write(s.getBytes());
    }
    
    public void flush() throws SugarException {
        if (satFileChannel == null) {
            return;
            /*
            if (fileSize == 0)
                return;
            else
                throw new SugarException("Internal error: flushing unopened file " + satFileName);
             */
        }
        try {
            satByteBuffer.flip();
            satFileChannel.write(satByteBuffer);
            satByteBuffer.clear();
        } catch (IOException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }

    public void close() throws SugarException {
        if (satFileChannel == null) {
            return;
            /*
            if (fileSize == 0)
                return;
            else
                throw new SugarException("Internal error: closing unopened file " + satFileName);
            */
        }
        try {
            flush();
            satFileChannel.close();
            satFileChannel = null;
            satByteBuffer = null;
        } catch (IOException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }
    
    public void update() throws SugarException {
        int n = 64;
        StringBuilder s = new StringBuilder();
        if (groups > 0) {
            if (GCNF) {
                s.append("p gcnf ");
                s.append(Integer.toString(variablesCount));
                s.append(" ");
                s.append(Integer.toString(clausesCount));
                s.append(" ");
                s.append(Integer.toString(groups));
            } else if (GWCNF) {
                s.append("p gwcnf ");
                s.append(Integer.toString(variablesCount));
                s.append(" ");
                s.append(Integer.toString(clausesCount));
                s.append(" ");
                s.append(Integer.toString(topWeight));
            } else {
                throw new SugarException("GCNF or GWCNF format should be used");
            }
        } else {
            s.append("p cnf ");
            s.append(Integer.toString(variablesCount));
            s.append(" ");
            s.append(Integer.toString(clausesCount));
        }
        while (s.length() < n - 1) {
            s.append(" ");
        }
        s.append("\n");
        String header = s.toString();
        if (satFileChannel != null)
            throw new SugarException("Internal error: updating opening file " + satFileName);
        try {
            RandomAccessFile satFile1 = new RandomAccessFile(satFileName, "rw");
            satFile1.seek(0);
            satFile1.write(header.getBytes());
            if (fileSize == 0)
                fileSize = header.length();
            satFile1.setLength(fileSize);
            satFile1.close();
        } catch (IOException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }
    
    public void clear() throws SugarException {
        super.clear();
        update();
    }
    
    public void commit() throws SugarException {
        super.commit();
    }
    
    public void cancel() throws SugarException {
        done();
        super.cancel();
        update();
    }
    
    public void done() throws SugarException {
        if (clausesCount == 0) {
            if (variablesCount == 0)
                variablesCount++;
            addNormalizedClause(new int[] { 1, -1 });
            clausesCount++;
        }
        flush();
        close();
        update();
    }
    
    public void addComment(String comment) throws SugarException {
        if (SugarMain.debug >= 1)
            write("c " + comment + "\n");
    }
    
    public void addPragmaDominant(int code0, int code1) throws SugarException {
        write("c " + SugarConstants.PRAGMA + " " + PRAGMA_DOMINANT + " " + code0 + " " + code1 + "\n");
    }
    
    public void addNormalizedClause(int[] clause) throws SugarException {
        if (GCNF) {
            if (groupsString == null)
                write("{0} ");
            else
                write("{" + groupsString + "} ");
        } else if (GWCNF) {
            if (groupsString == null)
                write("0 " + topWeight + " ");
            else
                write(groupsString + " " + weightString + " ");
        }
        for (int code : clause)
            write(Integer.toString(code) + " ");
        write("0\n");
    }

}
