package my.com.engpeng.engpengsalesorder.database.branch;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BranchDao {
    @Query("SELECT * FROM branch ORDER BY id")
    LiveData<List<BranchEntry>> loadLiveAllBranches();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBranch(BranchEntry branchEntry);

    @Query("DELETE FROM branch")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM branch")
    LiveData<Integer> getLiveCount();

    @Query("SELECT * FROM branch WHERE id = :id")
    LiveData<BranchEntry> loadLiveBranchById(Long id);

    @Query("SELECT * FROM branch WHERE company_id = 0")
    LiveData<List<BranchEntry>> loadLiveAllCompany();

    @Query("SELECT * FROM branch WHERE id = :id")
    BranchEntry loadBranchById(Long id);
}
