package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepository {
    public void save(Groups group) {
        // 그룹 저장, 예외처리?
    }

    public Groups findById(Long id) {
        // 그룹 조회
        return null;
    }

    public void deleteGroup(Groups g) {
        // 그룹 삭제 : id? 그룹 객체?, 예외 처리?
    }
}
