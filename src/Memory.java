import java.util.*;

class Partition {
    private int partitionNum;       //分区区号
    private int partitionSize;
    private int startAddr;
    private int Status;
    private Process process;

    {
        Status = 0;
        partitionSize = 0;
        process = null;
    }

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    public int getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(int partitionSize) {
        this.partitionSize = partitionSize;
    }

    public int getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(int startAddr) {
        this.startAddr = startAddr;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}

public class Memory {
    private int memorySize;
    private int partitionNum;       //分区数
    List<Partition> availableList;
    List<Partition> partitionList;

    public Memory(int memorySize) {
        this.memorySize = memorySize;
        init();
        partitionNum = 1;


    }

    public void init(){
        availableList = new ArrayList<>();
        partitionList = new ArrayList<>();

        Partition partition = new Partition();
        partition.setPartitionNum(1);
        partition.setStatus(0);
        partition.setStartAddr(0);
        partition.setPartitionSize(memorySize);
        partitionList.add(partition);
        availableList.add(partition);
    }

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }


}
