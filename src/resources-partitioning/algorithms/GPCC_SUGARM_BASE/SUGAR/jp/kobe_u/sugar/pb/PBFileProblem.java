package jp.kobe_u.sugar.pb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;


public class PBFileProblem extends PBProblem {
    public static int PB_BUFFER_SIZE = 4*1024;
    
    public static long MAX_PB_SIZE = 3*1024*1024*1024L;
    
    private String pbFileName;

    private FileChannel pbFileChannel = null; 

    private ByteBuffer pbByteBuffer = null;
    
    public PBFileProblem(String pbFileName) throws SugarException {
        this.pbFileName = pbFileName;
        clear();
    }

    public void open() throws SugarException {
        if (pbFileChannel != null)
            throw new SugarException("Internal error: re-opening file " + pbFileName);
        try {
            if (fileSize == 0) {
                pbFileChannel = (new FileOutputStream(pbFileName)).getChannel();
            } else {
                pbFileChannel = (new RandomAccessFile(pbFileName, "rw")).getChannel();
                pbFileChannel.position(fileSize);
            }
            pbByteBuffer = ByteBuffer.allocateDirect(PB_BUFFER_SIZE);
        } catch (IOException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }

    public void write(byte[] b) throws SugarException {
        if (pbFileChannel == null)
            open();
        int len = b.length;
        if (pbByteBuffer.position() + len > PB_BUFFER_SIZE)
            flush();
        int pos = 0;
        while (len > PB_BUFFER_SIZE) {
            pbByteBuffer.put(b, pos, PB_BUFFER_SIZE);
            flush();
            pos += PB_BUFFER_SIZE;
            len -= PB_BUFFER_SIZE;
        }
        pbByteBuffer.put(b, pos, len);
        fileSize += b.length;
        /*if (fileSize >= MAX_PB_SIZE) {
            (new File(pbFileName)).delete();
            throw new SugarException("Encoding is interrupted because file size becomes too large (" + fileSize + " bytes)");
        }*/
    }
    
    public void write(String s) throws SugarException {
        write(s.getBytes());
    }
    
    public void flush() throws SugarException {
        if (pbFileChannel == null) {
            return;
        }
        try {
            pbByteBuffer.flip();
            pbFileChannel.write(pbByteBuffer);
            pbByteBuffer.clear();
        } catch (IOException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }

    public void close() throws SugarException {
        if (pbFileChannel == null) {
            return;
        }
        try {
            flush();
            pbFileChannel.close();
            pbFileChannel = null;
            pbByteBuffer = null;
        } catch (IOException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }
    
    public void update() throws SugarException {
        int n = 64;
        StringBuilder s = new StringBuilder();
        s.append("* #variable= ");
        s.append(Integer.toString(variablesCount));
        s.append(" #constraint= ");
        s.append(Integer.toString(constraintsCount));
        while (s.length() < n - 1) {
            s.append(" ");
        }
        s.append("\n");
        String header = s.toString();
        if (pbFileChannel != null)
            throw new SugarException("Internal error: updating opening file " + pbFileName);
        try {
            RandomAccessFile satFile1 = new RandomAccessFile(pbFileName, "rw");
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
        if (constraintsCount == 0) {
            if (variablesCount == 0)
                variablesCount++;
            PBExpr expr = new PBExpr();
            expr.add(1, 1);
            expr.setCmp(">=");
            expr.setB(0);
            addPBConstraint(expr);
        }
        flush();
        close();
        update();
    }
    
    public void addComment(String comment) throws SugarException {
        if (SugarMain.debug >= 1)
            write("* " + comment + "\n");
    }
    
    public void addPBConstraint(PBExpr expr) throws SugarException {
        if (expr.isValid())
            return;
        if (expr.isUnsatisfiable()) {
            if (variablesCount == 0)
                variablesCount++;
            expr = new PBExpr();
            expr.add(-1, 1);
            expr.add(-1);
            expr.setCmp(">=");
        }
        write(expr.toString());
        write(" ;\n");
        constraintsCount++;
    }

    public void addMinExpr(PBExpr minExpr) throws SugarException {
        write("min: ");
        write(minExpr.toString());
        write(" ;\n");
    }

    public void addPMin(String pmin) throws SugarException {
        write("* pmin:");
        write(pmin);
        write(" ;\n");
    }
}
