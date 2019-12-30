import consoletable.ConsoleTable;
import consoletable.enums.NullPolicy;
import consoletable.table.Cell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OS {
    private static Memory memory;
    static List<Process> requestList = new ArrayList<>();

    public static void setMemory(int memorySize){
        memory = new Memory(memorySize);
    }

    public static Memory getMemory() {
        return memory;
    }

    public static void addProcess(String processName, int processSize ){
        Process process = new Process(processName, processSize);
        requestList.add(process);
    }

    public static void printRequestList(){
        List<Cell> header = new ArrayList<>(){{
            add(new Cell("进程名"));
            add(new Cell("进程大小"));
        }};

        List<List<Cell>> body = new ArrayList<>();
        List<Cell> bodyCell;

        for (Process process :
                requestList) {
            bodyCell = new ArrayList<>();
            bodyCell.add(new Cell(String.valueOf(process.getProcessName())));
            bodyCell.add(new Cell(process.getSize() +"K"));
            body.add(bodyCell);
        }

        new ConsoleTable.ConsoleTableBuilder()
                .addHeaders(header)
                .addRows(body)
                .nullPolicy(NullPolicy.NULL_STRING)
                .build()
                .print();
    }

    public static boolean FirstFit(){
        Process process = requestList.get(0);
        orderAvailableListByAddr();
        if(Distribute(process)){
            requestList.remove(process);

            printPartitionList();
            return true;
        }
        else
            return false;


    }

    public static boolean BestFit(){
        Process process;
        orderAvailableListBySize();
//        while(requestList.size() != 0){
//            process = requestList.get(0);
//            if(!canfenpei(process))
//                return false;
//            requestList.remove(process);
//            printPartitionList();
//
//        }
        process = requestList.get(0);
        if(Distribute(process)){
            requestList.remove(process);
            printPartitionList();
            return true;
        }
        else
            return false;


    }

    public static int WorstFit(){
        if(memory.availableList.isEmpty())
            return 0;

        Process process = requestList.get(0);
        reverseAvailableListBySize();

        Partition partition = memory.availableList.get(0);
        int partitionSize = partition.getPartitionSize();
        if (partition.getPartitionSize() < process.getSize())
            return -1;

        if(partition.getPartitionSize() == process.getSize()){
            memory.availableList.remove(0);
            requestList.remove(process);
            partition.setStatus(1);
            partition.setPartitionSize(process.getSize());
            partition.setProcess(process);
            orderPartitionListByAddr();
            printPartitionList();
        }else if(partition.getPartitionSize() > process.getSize()){
            memory.availableList.remove(0);
            requestList.remove(process);
            partition.setStatus(1);
            partition.setPartitionSize(process.getSize());
            partition.setProcess(process);

            Partition newPartition = new Partition();
            newPartition.setStartAddr(partition.getStartAddr()+process.getSize());
            newPartition.setPartitionSize(partitionSize - process.getSize());

            memory.availableList.add(newPartition);
            memory.partitionList.add(newPartition);
            memory.setPartitionNum(memory.partitionList.size());
            orderPartitionListByAddr();
            printPartitionList();

        }
        return 1;
    }

    private static boolean Distribute(Process process) {
        Partition partition;
        int partitionSize;
        for (int i = 0; i < memory.availableList.size(); i++) {
            partition = memory.availableList.get(i);
            partitionSize = partition.getPartitionSize();
            if(partitionSize > process.getSize()){
                memory.availableList.remove(i);
                partition.setStatus(1);
                partition.setPartitionSize(process.getSize());
                partition.setProcess(process);

                Partition newPartition = new Partition();
                newPartition.setStartAddr(partition.getStartAddr()+process.getSize());
                newPartition.setPartitionSize(partitionSize - process.getSize());

                memory.availableList.add(newPartition);
                memory.partitionList.add(newPartition);
                memory.setPartitionNum(memory.partitionList.size());
                orderPartitionListByAddr();

                return true;
            }
            else if(partitionSize == process.getSize()){
                partition.setStatus(1);
                partition.setProcess(process);
                memory.availableList.remove(i);
                orderPartitionListByAddr();
                return true;
            }
        }
        return false;
    }

    public static void mergeMemory(int partitionNum){
        if(partitionNum > memory.getPartitionNum())
            return;
        Partition thisPartition = memory.partitionList.get(partitionNum-1);
        if(thisPartition.getStatus() == 0)
            return;
        if (partitionNum == 1){
            if(memory.partitionList.size() == 1){
                release(thisPartition);
                return;
            }
            Partition afterPartition = memory.partitionList.get(partitionNum);
            if(afterPartition.getStatus() == 0){
                merge(thisPartition,afterPartition);
            }else{
                release(thisPartition);
            }
        }else if (partitionNum == memory.getPartitionNum()){
            Partition beforePartition = memory.partitionList.get(partitionNum-2);

            if(beforePartition.getStatus() == 0){
                merge(thisPartition,beforePartition);
            }else{
                release(thisPartition);
            }

        }
        else {
            Partition afterPartition = memory.partitionList.get(partitionNum);
            Partition beforePartition = memory.partitionList.get(partitionNum-2);


            if(beforePartition.getStatus() == 0 && afterPartition.getStatus() == 0 ){
                merge(beforePartition,thisPartition,afterPartition);
            }else if(beforePartition.getStatus() == 0){
                merge(thisPartition,beforePartition);
            }else if(afterPartition.getStatus() == 0){
                merge(thisPartition,afterPartition);
            }else{
                release(thisPartition);
            }
        }
    }

    private static void release(Partition thisPartition){
        thisPartition.setProcess(null);
        thisPartition.setPartitionSize(thisPartition.getPartitionSize());
        thisPartition.setStartAddr(thisPartition.getStartAddr());
        thisPartition.setStatus(0);
        memory.availableList.add(thisPartition);
    }

    private static void merge(Partition p1, Partition p2) {
        int partitionNum = memory.getPartitionNum();

        Partition partition = new Partition();
        partition.setProcess(null);
        partition.setPartitionSize(p1.getPartitionSize() + p2.getPartitionSize());
        partition.setStartAddr(Math.min(p1.getStartAddr(), p2.getStartAddr()));
        partition.setStatus(0);
        memory.availableList.remove(p2);
        memory.availableList.add(partition);

        memory.setMemorySize(--partitionNum);
        memory.partitionList.remove(p1);
        memory.partitionList.remove(p2);
        memory.partitionList.add(partition);

        orderPartitionListByAddr();

    }

    public static void merge(Partition p1, Partition p2,Partition p3) {
        int partitionNum = memory.getPartitionNum();
        Partition partition = new Partition();
        partition.setPartitionSize(p1.getPartitionSize() + p2.getPartitionSize()+p3.getPartitionSize());
        partition.setStartAddr(Math.min(p1.getStartAddr(), p2.getStartAddr()));
        partition.setStartAddr(Math.min(partition.getStartAddr(),p3.getStartAddr()));
        partition.setStatus(0);
        memory.availableList.remove(p1);
        memory.availableList.remove(p3);
        memory.availableList.add(partition);

        memory.setMemorySize(partitionNum-2);
        memory.partitionList.remove(p1);
        memory.partitionList.remove(p2);
        memory.partitionList.remove(p3);
        memory.partitionList.add(partition);

        orderPartitionListByAddr();
    }

    public static void orderAvailableListByAddr(){
        memory.availableList.sort(Comparator.comparingInt(Partition::getStartAddr));
        for (int i = 0; i < memory.availableList.size(); i++) {
            memory.availableList.get(i).setPartitionNum(i+1);
        }
    }

    public static void orderAvailableListBySize(){
        memory.availableList.sort(Comparator.comparingInt(Partition::getPartitionSize));

    }

    public static void reverseAvailableListBySize(){
        memory.availableList.sort(Comparator.comparingInt(Partition::getPartitionSize).reversed());
    }

    public static void orderPartitionListByAddr(){
        memory.partitionList.sort(Comparator.comparingInt(Partition::getStartAddr));
        for (int i = 0; i < memory.partitionList.size(); i++) {
            memory.partitionList.get(i).setPartitionNum(i+1);
        }
    }

    public static void printAvailableList(){
        for (int i = 0; i < memory.availableList.size(); i++) {
            System.out.println(memory.availableList.get(i));
        }
    }

    public static void printPartitionList(){
        List<Cell> header = new ArrayList<>(){{
            add(new Cell("区号"));
            add(new Cell("首地址"));
            add(new Cell("分区大小"));
            add(new Cell("状态"));
            add(new Cell("占用进程名"));
        }};

        List<List<Cell>> body = new ArrayList<>();
        List<Cell> bodyCell;

        for (Partition partition:
                memory.partitionList) {
//            System.out.println(partition);
            bodyCell = new ArrayList<>();
            bodyCell.add(new Cell(String.valueOf(partition.getPartitionNum())));
            bodyCell.add(new Cell(String.valueOf(partition.getStartAddr())));
            bodyCell.add(new Cell(partition.getPartitionSize()+"K"));
            if(partition.getStatus() == 1)
                bodyCell.add(new Cell("占用"));
            else
                bodyCell.add(new Cell("空闲"));

            if (partition.getProcess() != null)
                bodyCell.add(new Cell(String.valueOf(partition.getProcess().getProcessName())));
            else
                bodyCell.add(new Cell(""));
            body.add(bodyCell);
        }

        new ConsoleTable.ConsoleTableBuilder()
                .addHeaders(header)
                .addRows(body)
                .build()
                .print();
    }

}
