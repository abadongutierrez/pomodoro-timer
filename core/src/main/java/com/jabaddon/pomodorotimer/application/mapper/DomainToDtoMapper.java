package com.jabaddon.pomodorotimer.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.jabaddon.pomodorotimer.application.dto.*;
import com.jabaddon.pomodorotimer.domain.model.DailyStatistics;
import com.jabaddon.pomodorotimer.domain.model.FinishReason;
import com.jabaddon.pomodorotimer.domain.model.PauseRecord;
import com.jabaddon.pomodorotimer.domain.model.SessionType;
import com.jabaddon.pomodorotimer.domain.model.TimerRecord;
import com.jabaddon.pomodorotimer.domain.model.TimerState;

/**
 * Mapper utility for converting between domain models and DTOs.
 * The TimerApplicationService uses this to translate domain objects
 * before passing them to ports (adapters).
 */
public final class DomainToDtoMapper {

    private DomainToDtoMapper() {
        // Utility class
    }

    // ========== Domain to DTO ==========

    public static SessionTypeDTO toDto(SessionType sessionType) {
        if (sessionType == null) {
            return null;
        }
        return switch (sessionType) {
            case WORK -> new SessionTypeDTO(
                SessionTypeEnumDTO.WORK, sessionType.getDefaultMinutes(), sessionType.getDisplayName());
            case SHORT_BREAK -> new SessionTypeDTO(
                SessionTypeEnumDTO.SHORT_BREAK, sessionType.getDefaultMinutes(), sessionType.getDisplayName());
            case LONG_BREAK -> new SessionTypeDTO(
                SessionTypeEnumDTO.LONG_BREAK, sessionType.getDefaultMinutes(), sessionType.getDisplayName());
        };
    }

    public static TimerStateDTO toDto(TimerState state) {
        if (state == null) {
            return null;
        }
        return switch (state) {
            case IDLE -> TimerStateDTO.IDLE;
            case READY -> TimerStateDTO.READY;
            case RUNNING -> TimerStateDTO.RUNNING;
            case PAUSED -> TimerStateDTO.PAUSED;
            case COMPLETED -> TimerStateDTO.COMPLETED;
        };
    }

    public static FinishReasonDTO toDto(FinishReason reason) {
        if (reason == null) {
            return null;
        }
        return FinishReasonDTO.valueOf(reason.name());
    }

    public static PauseRecordDTO toDto(PauseRecord pauseRecord) {
        if (pauseRecord == null) {
            return null;
        }
        return new PauseRecordDTO(
            pauseRecord.getPausedAt(),
            pauseRecord.getUnpausedAt()
        );
    }

    public static TimerRecordDTO toDto(TimerRecord record) {
        if (record == null) {
            return null;
        }

        List<PauseRecordDTO> pauseRecordDTOs = record.getPauseRecords().stream()
            .map(DomainToDtoMapper::toDto)
            .collect(Collectors.toList());

        return new TimerRecordDTO(
            record.getStartedAt(),
            record.getFinishedAt(),
            toDto(record.getReason()),
            toDto(record.getSessionType()),
            record.getDurationMinutes(),
            record.getDescription(),
            pauseRecordDTOs
        );
    }

    public static DailyStatisticsDTO toDto(DailyStatistics statistics) {
        if (statistics == null) {
            return null;
        }
        return new DailyStatisticsDTO(
            statistics.getDate(),
            statistics.getCompletedPomodoros(),
            statistics.getCurrentCycle()
        );
    }

    // ========== DTO to Domain ==========

    public static SessionType toDomain(SessionTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        return SessionType.valueOf(dto.sessionType().name());
    }

    public static FinishReason toDomain(FinishReasonDTO dto) {
        if (dto == null) {
            return null;
        }
        return FinishReason.valueOf(dto.name());
    }

    public static PauseRecord toDomain(PauseRecordDTO dto) {
        if (dto == null) {
            return null;
        }
        return new PauseRecord(
            dto.getPausedAt(),
            dto.getUnpausedAt()
        );
    }

    public static TimerRecord toDomain(TimerRecordDTO dto) {
        if (dto == null) {
            return null;
        }

        List<PauseRecord> pauseRecords = dto.getPauseRecords().stream()
            .map(DomainToDtoMapper::toDomain)
            .collect(Collectors.toList());

        return new TimerRecord(
            dto.getStartedAt(),
            dto.getFinishedAt(),
            toDomain(dto.getReason()),
            toDomain(dto.getSessionType()),
            dto.getDurationMinutes(),
            dto.getDescription(),
            pauseRecords
        );
    }

    public static DailyStatistics toDomain(DailyStatisticsDTO dto) {
        if (dto == null) {
            return null;
        }
        return new DailyStatistics(
            dto.getDate(),
            dto.getCompletedPomodoros()
        );
    }
}
