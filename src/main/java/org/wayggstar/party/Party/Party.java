package org.wayggstar.party.Party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private String name;
    private UUID leader;
    private List<UUID> members;

    public Party(String name, UUID leader){
        this.name = name;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.members.add(leader);
    }

    public String getName() {
        return name;
    }

    public UUID getLeader(){
        return leader;
    }

    public boolean changeLeader(UUID newLeader) {
        if (!members.contains(newLeader)) {
            return false;
        }
        this.leader = newLeader;
        return true;
    }

    public List<UUID> getMembers(){
        return members;
    }

    public void addMember(UUID member){
        if (!members.contains(member)){
            members.add(member);
        }
    }

    public void removeMember(UUID member){
        members.remove(member);
    }

    public boolean isMember(UUID member){
        return members.contains(member);
    }
}
