import java.util.Scanner;

public class Shell {
    static Scanner sc = new Scanner(System.in);

    public static void Distribute(int i){
        if (OS.requestList.size() == 0)
            System.out.println(">ERROR:当前无进程请求");
        else{
            switch (i){
                case 1:
                    if(OS.FirstFit()){
                        System.out.println(">分配成功");
                    }else {
                        System.out.println(">ERROR:无法为所有进程分配");
                    }
                    break;
                case 2:
                    if(OS.BestFit()){
                        System.out.println(">分配成功");
                    }else {
                        System.out.println(">ERROR:无法为所有进程分配");
                    }
                    break;
                case 3:
                    if(OS.WorstFit() == 1){
                        System.out.println(">分配成功");
                    }else if(OS.WorstFit() == 0){
                        System.out.println(">ERROR:可用表为空，无可用分区");
                    }else if(OS.WorstFit() == -1){
                        System.out.println(">ERROR:无可分配分区");
                    }
                    break;

            }
        }

    }

    public static void Release(){
        int i;
        while(true) {

            OS.printPartitionList();
            System.out.print(">请输入释放分区的区号（输入0返回上一层）:");
            i = Integer.parseInt(sc.next());
            if (i > 0 && i <= OS.getMemory().getPartitionNum()) {
                System.out.println(">释放分区号为：" + i);
                OS.mergeMemory(i);
            }else if(i == 0){
                return;
            }
            else {
                System.out.println(">ERROR：输入分区区号错误");
            }
        }
    }

    public static void AddProcess(){
        while(true) {
            if(OS.requestList.size() != 0){
                OS.printRequestList();
            }

            System.out.print(">请输入分区名称（输入0返回上一层）:");
            String processNam = sc.next();

            if(processNam.equals("0")){
                return;
            }

            System.out.print(">请输入分区大小（K）：");
            int processSize = Integer.parseInt(sc.next());
            if(OS.getMemory().getMemorySize() < processSize){
                System.out.println(">ERROR：此进程过大，无法为其分配进程");
                continue;
            }
            OS.addProcess(processNam,processSize);
        }
    }

    public static void main(String[] args) {
        int i,op;
        System.out.println(">请初始化内存信息");
        System.out.print(">内存大小为（Size/K）：");
        OS.setMemory(Integer.parseInt(sc.next()));
        System.out.println(">分配分区方法：");
        System.out.println(">\t\t1.最先适应法\n"+
                " \t\t2.最佳适应法\n"+
                " \t\t3.最坏适应法");
        System.out.print(">");
        op = Integer.parseInt(sc.next());

        while(true){
            System.out.println(">\t\t1.查看分区说明表\n"+
                    " \t\t2.查看请求表\n"+
                    " \t\t3.添加进程（作业）\n"+
                    " \t\t4.分配分区\n"+
                    " \t\t5.释放分区\n"+
                    " \t\t0.退出程序");
            System.out.print(">您要执行的操作是：");
            try{
                i = Integer.parseInt(sc.next());
                switch (i){
                    case 1:
                        OS.printPartitionList();
                        break;

                    case 2:
                        if (OS.requestList.size() == 0){
                            System.out.println(">ERROR:暂无进程请求");
                            break;
                        }

                        OS.printRequestList();
                        break;
                    case 3:
                        AddProcess();
                        break;
                    case 4:
                        Distribute(op);
                        break;
                    case 5:
                        Release();
                        break;
                    case 0:
                        sc.close();
                        return;
                    default:
                        System.out.println(">ERROR：输入格式错误，请重新输入");
                }
            }catch (NumberFormatException e){
                System.out.println(">ERROR：输入格式错误，请重新输入");
            }
        }
    }
}
