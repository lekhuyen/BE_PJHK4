package fpt.aptech.server_be.repositories;

import fpt.aptech.server_be.entities.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
}
