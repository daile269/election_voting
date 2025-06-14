package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.dto.VoteDTO;
import com.datn.electronic_voting.entity.Election;
import com.datn.electronic_voting.entity.ElectionCandidate;
import com.datn.electronic_voting.entity.User;
import com.datn.electronic_voting.entity.Vote;
import com.datn.electronic_voting.enums.ElectronStatus;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.mapper.VoteMapper;
import com.datn.electronic_voting.repositories.*;
import com.datn.electronic_voting.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    private final ElectionRepository electionRepository;

    private final UserRepository userRepository;

    private final CandidateRepository candidateRepository;

    private final VoteMapper voteMapper;

    private final ElectionCandidateRepository electionCandidateRepository;

    private final VoteChoiceCache voteChoiceCache;

    private final String GENERATOR ="03FB32C9B73134D0B2E77506660EDBD484CA7B18F21EF205407F4793A1A0BA12510DBC15077BE463FFF4FED4AAC0BB555BE3A6C1B0C6B47B1BC3773BF7E8C6F62901228F8C28CBB18A55AE31341000A650196F931C77A57F2DDF463E5E9EC144B777DE62AAAB8A8628AC376D282D6ED3864E67982428EBC831D14348F6F2F9193B5045AF2767164E1DFC967C1FB3F2E55A4BD1BFFE83B9C80D052B985D182EA0ADB2A3B7313D3FE14C8484B1E052588B9B7D2BBD2DF016199ECD06E1557CD0915B3353BBB64E0EC377FD028370DF92B52C7891428CDC67EB6184B523D1DB246C32F63078490F00EF8D647D148D47954515E2327CFEF98C582664B4C0F6CC41659";

    private final String PRIME ="087A8E61DB4B6663CFFBBD19C651959998CEEF608660DD0F25D2CEED4435E3B00E00DF8F1D61957D4FAF7DF4561B2AA3016C3D91134096FAA3BF4296D830E9A7C209E0C6497517ABD5A8A9D306BCF67ED91F9E6725B4758C022E0B1EF4275BF7B6C5BFC11D45F9088B941F54EB1E59BB8BC39A0BF12307F5C4FDB70C581B23F76B63ACAE1CAA6B7902D52526735488A0EF13C6D9A51BFA4AB3AD8347796524D8EF6A167B5A41825D967E144E5140564251CCACB83E6B486F6B3CA3F7971506026C0B857F689962856DED4010ABD0BE621C3A3960A54E710C375F26375D7014103A4B54330C198AF126116D2276E11715F693877FAD7EF09CADB094AE91E1A1597";

    private final BigInteger g = new BigInteger(GENERATOR, 16);

    private final BigInteger p = new BigInteger(PRIME,16);

    private final BigInteger q = new BigInteger("08CF83642A709A097B447997640129DA299B1A47D1EB3750BA308B0FE64F5FBD3", 16);

    @Override
    public VoteDTO createVote(VoteDTO voteDTO, boolean voteChoice) {
        Vote vote = voteMapper.toEntity(voteDTO);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        vote.setUserId(user.getId());
        checkInforVote(vote);
        if(voteRepository.existsByUserIdAndElectionIdAndCandidateId(vote.getUserId(), vote.getElectionId(),vote.getCandidateId())) {
            throw new AppException(ErrorCode.VOTE_ALREADY_EXISTS);
        }
//      Sinh x_i ngẫu nhiên với từng vote
        SecureRandom random = new SecureRandom();
        BigInteger x_i = new BigInteger(q.bitLength(), random).mod(q);

        // Tính gx = g^x_i mod p
        BigInteger g_xi = g.modPow(x_i, p);

        vote.setX(x_i.toString());
        vote.setGx(g_xi.toString());

//        Lưu x_i và g_xi trước
        Vote savedVote= voteRepository.save(vote);
//        Giá trij phiếu vote
        voteChoiceCache.putVoteChoice(vote.getElectionId(), vote.getCandidateId(), user.getId(), voteChoice);
//        Kiểm tra số lượng phiếu
        int submitted = voteRepository.countVoteCandidateInElection(vote.getElectionId(), vote.getCandidateId());
        int expected = userRepository.countUserInElection(vote.getElectionId());

        if(submitted == expected){
            Map<Long, Boolean> voteChoices = voteChoiceCache.getVoteChoices(vote.getElectionId(), vote.getCandidateId());
            encryptedVote(vote,voteChoices);
            voteChoiceCache.clearVoteChoices(vote.getElectionId(), vote.getCandidateId());
        }
//        Đếm số lượng candidate đã được bỏ phiếu
        int totalUser = userRepository.countUserInElection(vote.getElectionId());
        int totalVotes  = voteRepository.countVoteCandidateInElection(vote.getElectionId(), vote.getCandidateId());
        if(totalVotes==totalUser){
            Election election = electionRepository.findById(vote.getElectionId())
                    .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
            election.setStatus(ElectronStatus.FINISHED);
            electionRepository.save(election);
        }
        return voteMapper.toDTO(vote);
    }

    @Override
    public int countAgreeVotes(Long electionId, Long candidateId) {
        int submitted = voteRepository.countVoteCandidateInElection(electionId, candidateId);
        int expected = userRepository.countUserInElection(electionId);
        if(submitted==expected){
            BigInteger encryptedTotal = BigInteger.ONE;

            List<Vote> voteList = voteRepository.findAllByElectionIdAndCandidateId(electionId,candidateId);
            if(voteList.isEmpty()) return 0;

            for (Vote vote : voteList) {
                encryptedTotal = encryptedTotal.multiply(new BigInteger(vote.getEncryptedVote())).mod(p);
            }

            BigInteger gk = encryptedTotal;
            return shanks(g, gk, p,voteList.size()); // Trả về số phiếu đồng ý
        }else return 0;

    }

    @Override
    public VoteDTO updateVote(VoteDTO voteDTO,Long id,boolean voteChoice) {
        Vote vote = voteMapper.toEntity(voteDTO);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return voteMapper.toDTO(voteRepository.save(vote));
    }

    @Override
    public List<VoteDTO> getAllVotes() {
        return voteRepository.findAll()
                .stream().map(vote -> voteMapper.toDTO(vote)).collect(Collectors.toList());
    }

    @Override
    public List<VoteDTO> getVotesPageable(Pageable pageable) {
        return voteRepository.findAll(pageable).getContent()
                .stream().map(vote -> voteMapper.toDTO(vote)).collect(Collectors.toList());
    }

    @Override
    public List<VoteDTO> getVoteByUserId(Long userId,Pageable pageable) {
        return voteRepository.getVoteByUserId(userId,pageable)
                .stream().map(vote -> voteMapper.toDTO(vote)).collect(Collectors.toList());
    }

    @Override
    public VoteDTO findVoteById(Long id) {
        Vote vote = voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        return voteMapper.toDTO(vote);
    }

    @Override
    public void deleteVote(Long id) {
        voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        voteRepository.deleteById(id);
    }

    @Override
    public List<VoteDTO> getVoteByElectionAndCandidateId(Long electionId,Long candidateId) {
        List<Vote> voteList = voteRepository.findAllByElectionIdAndCandidateId(electionId,candidateId);
        return voteList.stream()
                .map(vote -> voteMapper.toDTO(vote)).collect(Collectors.toList());
    }

    @Override
    public List<VoteDTO> getVotesByElectionId(Long electionId) {
        List<Vote> voteList = voteRepository.getVotesByElectionId(electionId);
        return voteList.stream().map(vote -> voteMapper.toDTO(vote)).collect(Collectors.toList());
    }
    @Override
    public int countVoteCandidateInElection(Long electionId, Long candidateId) {
        electionRepository.findById(electionId).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        candidateRepository.findById(candidateId).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
        return voteRepository.countVoteCandidateInElection(electionId,candidateId);
    }



    @Override
    public int totalItem() {
        return (int) voteRepository.count();
    }

    @Override
    public int totalItemVotesForUser(Long userId) {
        List<Vote> voteList = voteRepository.getAllVoteByUserId(userId);
        return voteList.size();
    }

    private void checkInforVote(Vote vote){
        electionRepository.findById(vote.getElectionId())
                .orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        candidateRepository.findById(vote.getCandidateId())
                .orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));

    }

    public void encryptedVote(Vote vote,Map<Long, Boolean> voteChoices){

        // Lấy danh sách tất cả gx trước đó để tính gy
        List<Vote> votes = voteRepository.findAllByElectionIdAndCandidateId(vote.getElectionId(), vote.getCandidateId());

        List<BigInteger> gxList = votes.stream()
                .map(v -> new BigInteger(v.getGx()))
                .collect(Collectors.toList());


        for(int i=0; i<votes.size();i++){
            Vote vote_i = votes.get(i);
            BigInteger x_i = new BigInteger(vote_i.getX());

            // Tính tử số: product(gx_j) từ j = 0 đến i - 1
            BigInteger numerator = BigInteger.ONE;
            for (int j = 0; j < i; j++) {
                numerator = numerator.multiply(gxList.get(j)).mod(p);
            }
            // Tính mẫu số: product(gx_j) từ j = i + 1 đến n - 1
            BigInteger denominator = BigInteger.ONE;
            for (int j = i + 1; j < gxList.size(); j++) {
                denominator = denominator.multiply(gxList.get(j)).mod(p);
            }

            // Tính g^y_i = (numerator / denominator) mod p
            BigInteger g_yi = numerator.multiply(denominator.modInverse(p)).mod(p);

            BigInteger encryptedVote;

            Boolean voteChoice = voteChoices.get(vote_i.getUserId());

            if (voteChoice == null) {
                System.out.println("Không tìm thấy lựa chọn bỏ phiếu của userId = " + vote_i.getUserId());
                continue; // hoặc xử lý mặc định là không đồng ý
            }
            if (voteChoice) { // Nếu chọn đồng ý
                encryptedVote = g_yi.modPow(x_i, p).multiply(g).mod(p);
            } else { // Nếu chọn không đồng ý
                encryptedVote = g_yi.modPow(x_i, p).mod(p);
            }
            vote_i.setGy(g_yi.toString());
            vote_i.setEncryptedVote(encryptedVote.toString());
            voteRepository.save(vote_i);

        }
        int voteCount = voteRepository.countVoteCandidateInElection(vote.getElectionId(),vote.getCandidateId());
        ElectionCandidate result =  electionCandidateRepository.findByElectionIdAndCandidateId(vote.getElectionId(),vote.getCandidateId());
        result.setVoteCount(voteCount);
        electionCandidateRepository.save(result);

    }

    public static int shanks(BigInteger g, BigInteger h, BigInteger p,int bound) {
        int m = (int) Math.ceil(Math.sqrt(bound));
        Map<BigInteger, Integer> table = new HashMap<>();

        BigInteger gm = g.modPow(BigInteger.valueOf(m), p);
        BigInteger invGm = gm.modInverse(p);

        BigInteger curr = BigInteger.ONE;
        for (int j = 0; j <= m; j++) {
            table.put(curr, j);
            curr = curr.multiply(g).mod(p);
        }

        curr = h;
        for (int i = 0; i <= m; i++) {
            if (table.containsKey(curr)) {
                int j = table.get(curr);
                return i * m + j;
            }
            curr = curr.multiply(invGm).mod(p);
        }

        return -1; // Không tìm thấy
    }


}
