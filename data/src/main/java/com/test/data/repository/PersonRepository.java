package com.test.data.repository;

import com.test.data.domain.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Repository
public interface PersonRepository extends GraphRepository<Person> {
    @Query("MATCH (n:Person) WHERE ID(n) <> {id} RETURN n;")
    Iterable<Person> findByIdNot(@Param("id") Long id);

    //找出所有朋友包括路径
    @Query("MATCH shortestPath((n:Person)-[r:FRIEND_OF*]->(m:Person)) WHERE ID(n)={id} RETURN m, length(r) as path ORDER BY m.name")
    Set<Person> findFriendsById(@Param("id") Long id);

    //看过指定电影的所有朋友
    @Query("MATCH (n:Person)-[r:FRIEND_OF*]-(m:Person) WHERE id(n)={id} WITH  m MATCH (m)-[:VISITED]->()-[:BLOCKBUSTER]->({name:{name}}) " +
            "RETURN  distinct m ORDER BY m.name")
    Set<Person> findFriendsVisiterMovie(@Param("id") Long id, @Param("name") String name);

    //未看过指定电影的朋友
    @Query("MATCH (n:Person)-[:FRIEND_OF*1..3]->(m:Person) WHERE id(n)={id} AND NOT (m)-[:VISITED]->()-[:BLOCKBUSTER]->({name:{name}}) " +
            "AND id(m) <> {id} RETURN  m ORDER BY m.name")
    Set<Person> findFriendsNotVisiterMovie(@Param("id") Long id, @Param("name") String name);

    @Query("MATCH (n:Person)-[:FRIEND_OF*1..3]->(m:Person) WHERE id(n)={id} AND NOT (m)-[:VISITED]->()-[:BLOCKBUSTER]->({name:{name}}) " +
            "AND id(m) <> {id} RETURN  m ORDER BY m.name skip {skip} limit {limit}")
    Set<Person> findFriendsNotVisiterMoviePage(@Param("id") Long id, @Param("name") String name, @Param("skip")int skip, @Param("limit")int limit);


    //看过指定电影的用户
    @Query("MATCH (o:Person) WHERE (o)-[:VISITED]->()-[:BLOCKBUSTER]->({name:{name}}) RETURN o")
    Set<Person> findUsersByVisiterMovieName(@Param("name") String name);

    //没有看过指定电影的用户
    @Query("MATCH (o:Person) WHERE NOT (o)-[:VISITED]->()-[:BLOCKBUSTER]->({name:{name}}) RETURN o")
    Set<Person> findUsersByNotVisiterMovieName(@Param("name") String name);

    @Query("MATCH (o:Person) WHERE NOT (o)-[:VISITED]->()-[:BLOCKBUSTER]->({name:{name}}) RETURN o SKIP {skip} LIMIT {limit}")
    Set<Person> findUsersByNotVisiterMovieNamePage(@Param("name")String name, @Param("skip")int skip, @Param("limit")int limit);

//    @Query("MATCH (u1:Person)-[x:RATED]->(m:Movie),(u2:Person)-[y:RATED]->(m) WHERE id(u1)<id(u2) WITH sqrt(sum(x.stars-y.stars)^2) AS euc,u1,u2 MERGE (u1)->[d:DISTANCE]-(u2) SET d.euclidean=euc")
    Person findByName(String name);

    @Query("MATCH p=(m:Person)-[r:DISTANCE]->(n:Person) where id(m)={id} with r.euclidean as e,n order by e return n limit 1")
    Set<Person> find_similar_person( @Param("id")int id);

    Iterable<Person> findByNameLike(String name);

    Iterable<Person> findByCreateLessThan(Date create);

}