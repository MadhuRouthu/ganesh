package com.njpa.jamesORMRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.njpa.jamesORMModel.TAResumeUpload;
@Repository
public interface TAResumeUploadRepo extends JpaRepository<TAResumeUpload, Long> {

}
