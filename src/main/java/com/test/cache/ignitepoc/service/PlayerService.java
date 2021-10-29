package com.test.cache.ignitepoc.service;

import com.test.cache.ignitepoc.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.expiry.ExpiryPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.test.cache.ignitepoc.constant.CacheConstant.PLAYER;

@Service
@Slf4j
public class PlayerService {
    private final CacheManager cacheManager;
    private ExpiryPolicy defaultIgniteExpirePolicy;

    public PlayerService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public List<Player> textSearch(String text) {
        log.info("Search the index to find players where either name or team contains the text");
        IgniteCache<Long, Player> playerCache = Ignition.ignite().getOrCreateCache(PLAYER);
        TextQuery<Long, Player> textQuery = new TextQuery<>(Player.class, text);
        return searchPlayers(playerCache, textQuery);
    }

    public List<Player> fuzzySearch(String text) {
        log.info("Fuzzy search on index to find players where either name or team contains the text");
        IgniteCache<Long, Player> playerCache = Ignition.ignite().getOrCreateCache(PLAYER);
        TextQuery<Long, Player> textQuery = new TextQuery<>(Player.class, text + "~");
        return searchPlayers(playerCache, textQuery);
    }

    public List<Player> fuzzySearchOnSpecificField(String text, String fieldName) {
        log.info("Fuzzy search on index to find players with the given field name and text");
        IgniteCache<Long, Player> playerCache = Ignition.ignite().getOrCreateCache(PLAYER);
        TextQuery<Long, Player> textQuery = new TextQuery<>(Player.class, fieldName + ":" + text + "~");
        return searchPlayers(playerCache, textQuery);
    }

    public List<Player> scanQuerySearch(String text) {
        log.info("ScanQuery search to find players whose team contain the given text");
        IgniteCache<Long, Player> playerCache = Ignition.ignite().getOrCreateCache(PLAYER);
        ScanQuery<Long, Player> scanQuery = new ScanQuery<>((k, v) -> v.getTeam().equalsIgnoreCase(text));
        List<Player> players = new ArrayList<>();
        log.info("Query :: " + scanQuery);
        try (QueryCursor<Cache.Entry<Long, Player>> cursor = playerCache.query(scanQuery)) {
            Iterator<Cache.Entry<Long, Player>> iterator = cursor.iterator();
            while (iterator.hasNext()) {
                Cache.Entry<Long, Player> entry = iterator.next();
                players.add(entry.getValue());

            }
        }
        return players;
    }

    public List<Player> sqlQuerySearch(String text) {
        log.info("SqlQuery search to find players whose team contain the given text");
        IgniteCache<Long, Player> playerCache = Ignition.ignite().getOrCreateCache(PLAYER);
        Collection<QueryEntity> queryEntities = playerCache.getConfiguration(CacheConfiguration.class).getQueryEntities();
        String tableName = queryEntities.stream().findFirst().orElseThrow(() -> new IllegalArgumentException(PLAYER + "doesn't exists")).getTableName();
        SqlFieldsQuery sqlQuery = new SqlFieldsQuery("select * from " + tableName + " where team='" + text + "'");
        log.info("Query :: " + sqlQuery.getSql());
        List<List<?>> records = playerCache.query(sqlQuery).getAll();
        return records.stream().map(objects -> convert((List<Object>) objects)).collect(Collectors.toList());
    }

    private Player convert(List<Object> values) {
        return Player.builder().id((Long) values.get(0)).name(String.valueOf(values.get(1)))
                .team(String.valueOf(values.get(2)))
                .salary(Double.parseDouble(values.get(3).toString())).build();
    }


    private List<Player> searchPlayers(IgniteCache igniteCache, TextQuery<Long, Player> textQuery) {
        List<Player> players = new ArrayList<>();
        log.info("Query :: " + textQuery.getText());
        try (QueryCursor<Cache.Entry<Long, Player>> cursor = igniteCache.query(textQuery)) {
            for (Cache.Entry<Long, Player> entry : cursor) {
                players.add(entry.getValue());
            }
        }
        return players;
    }

    @PostConstruct
    private void populatePlayerCache() {
        log.info("Populating Players Cache !!!");
        Cache<Long, Player> playerCache = cacheManager.getCache(PLAYER);
        long id = 1l;
        playerCache.put(id, new Player(id++, "Gerard Piqué", "Barcelona", 111111111.00));
        playerCache.put(id, new Player(id++, "Leo Messi", "Barcelona", 99987655555.00));
        playerCache.put(id, new Player(id++, "Christiano Ronaldo", "Manchester United", 2000000.00));
        playerCache.put(id, new Player(id++, "Paul Pogba", "Manchester United", 1000000.00));
        playerCache.put(id, new Player(id++, "Neymar", "PSG", 99699999.00));
        playerCache.put(id, new Player(id++, "Kylian Mbappé", "PSG", 996977999.00));
        playerCache.put(id, new Player(id++, "Luis Suárez", "Atlético de Madrid", 578699.00));
        playerCache.put(id, new Player(id++, "Antoine Griezmann", "Atlético de Madrid", 558699.00));
    }
}
