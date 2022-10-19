package net.octopvp.aetheriacoremaster.master.votifier;

import com.vexsoftware.votifier.model.Vote;
import net.badbird5907.lightning.event.Event;

import java.util.Objects;

public final class VotifierEvent implements Event {
    private final Vote vote;

    public VotifierEvent(Vote vote) {
        this.vote = vote;
    }

    public Vote getVote() {
        return vote;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VotifierEvent) obj;
        return Objects.equals(this.vote, that.vote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vote);
    }

    @Override
    public String toString() {
        return "VotifierEvent[" +
                "vote=" + vote + ']';
    }


}
