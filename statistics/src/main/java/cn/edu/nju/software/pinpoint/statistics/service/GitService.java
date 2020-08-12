package cn.edu.nju.software.pinpoint.statistics.service;

import cn.edu.nju.software.pinpoint.statistics.entity.Git;

import java.util.List;

public interface GitService {

    List<Git> queryGitByAppId(String appId);

    List<Git> queryGit(Git git);
}
