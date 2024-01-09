package com.mit.fabricsdk.dao;
import java.util.List;
import com.mit.fabricsdk.entity.ChannelInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface ChannelInfoDao extends PagingAndSortingRepository<ChannelInfo, Long>, JpaSpecificationExecutor<ChannelInfo> {
    List<ChannelInfo> findByChannelName(String channelName,Sort sort);

    @Query("SELECT c.channelName, MAX(c.channelHeight) FROM ChannelInfo c GROUP BY c.channelName")
    List<Object[]> findMaxChannelHeightForEachChannel();

    @Query("SELECT c.id FROM ChannelInfo c WHERE c.channelHeight = (SELECT MAX(ci.channelHeight) FROM ChannelInfo ci WHERE ci.channelName = c.channelName)")
    List<Long> findIdsOfMaxHeightChannels();

    default List<ChannelInfo> findMaxHeightChannels() {
        List<Long> ids = findIdsOfMaxHeightChannels();
        return (List<ChannelInfo>) findAllById(ids);
    }
}
