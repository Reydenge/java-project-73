package hexlet.code.repository;

import com.querydsl.core.types.dsl.SimpleExpression;
import hexlet.code.model.QTask;
import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>, QuerydslBinderCustomizer<QTask> {

    Optional<Task> findByName(String name);

    @Override
    default void customize(QuerydslBindings bindings, QTask qTask) {
        bindings.bind(qTask.taskStatus.id).first(SimpleExpression::eq);
        bindings.bind(qTask.executor.id).first(SimpleExpression::eq);
        bindings.bind(qTask.labelIds.any().id).first(SimpleExpression::eq);
        bindings.bind(qTask.author.id).first(SimpleExpression::eq);
    };

}
