package ChattingApp.OtherClasses;

import java.util.ArrayList;
import java.util.List;

public class Group {
    //Each group of six friends one user 5 friends
    private String groupName;
    private String user, f1, f2, f3, f4, f5;
    private List<String> groupFriends;

    public Group(String groupName, String user, List<String> groupFriends) {
        //when creating the group
        this.groupName = groupName;
        this.user = user;
        this.groupFriends = groupFriends;
        switch (groupFriends.size()) {
            case 2:
                f1 = groupFriends.get(0);
                f2 = groupFriends.get(1);
                break;
            case 3:
                f1 = groupFriends.get(0);
                f2 = groupFriends.get(1);
                f3 = groupFriends.get(2);
                break;
            case 4:
                f1 = groupFriends.get(0);
                f2 = groupFriends.get(1);
                f3 = groupFriends.get(2);
                f4 = groupFriends.get(3);
                break;
            case 5:
                f1 = groupFriends.get(0);
                f2 = groupFriends.get(1);
                f3 = groupFriends.get(2);
                f4 = groupFriends.get(3);
                f5 = groupFriends.get(4);
                break;

        }

    }

    public Group(String groupName, String user, String f1, String f2, String f3, String f4, String f5) {
        //when reading group data
        this.groupName = groupName;
        this.user = user;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
        this.f5 = f5;
        groupFriends = new ArrayList<>();
        groupFriends.add(f1);
        groupFriends.add(f2);
        groupFriends.add(f3);
        groupFriends.add(f4);
        groupFriends.add(f5);
    }
//
//    public Group(String groupName, String user, String f1, String f2, String f3, String f4) {
//        this.groupName = groupName;
//        this.user = user;
//        this.f1 = f1;
//        this.f2 = f2;
//        this.f3 = f3;
//        this.f4 = f4;
//    }
//
//    public Group(String groupName, String user, String f1, String f2, String f3) {
//        this.groupName = groupName;
//        this.user = user;
//        this.f1 = f1;
//        this.f2 = f2;
//        this.f3 = f3;
//
//    }
//
//    public Group(String groupName, String user, String f1, String f2) {
//        this.groupName = groupName;
//        this.user = user;
//        this.f1 = f1;
//        this.f2 = f2;
//    }


    public String getFriends() {
        switch (groupFriends.size()) {
            case 2:
                //////////////////// get friends depending on size of list.... to be shown on top label....
                return groupFriends.get(0) + ", " + groupFriends.get(1);
            case 3:
                return groupFriends.get(0) + ", " + groupFriends.get(1) + ", " + groupFriends.get(2);
            case 4:
                return groupFriends.get(0) + ", " + groupFriends.get(1) + ", " + groupFriends.get(2) + ", " + groupFriends.get(3);
            case 5:
                return groupFriends.get(0) + ", " + groupFriends.get(1) + ", " + groupFriends.get(2) + ", " + groupFriends.get(3) + ", " + groupFriends.get(4);
        }
        return null;
    }

    public List<String> getGroupFriendsList() {
        return this.groupFriends;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getF1() {
        if (f1 != null) {
            return f1;
        }
        return "";
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        if (f2 != null) {
            return f2;
        }
        return "";
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        if (f3 != null) {
            return f3;
        }
        return "";
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public String getF4() {
        if (f4 != null) {
            return f4;
        }
        return "";
    }

    public void setF4(String f4) {
        this.f4 = f4;
    }

    public String getF5() {
        if (f5 != null) {
            return f5;
        }
        return "";
    }

    public void setF5(String f5) {
        this.f5 = f5;
    }

}
