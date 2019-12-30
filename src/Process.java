public class Process {
    private int size;
    private String processName;

    public Process(String processName,int size ) {
        setSize(size);
        setProcessName(processName);
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size){
        this.size = size;
    }

    @Override
    public String toString() {
        return getProcessName();
    }
}
