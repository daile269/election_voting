package com.datn.electronic_voting.service.impl;

import com.datn.electronic_voting.entity.Vote;
import com.datn.electronic_voting.exception.AppException;
import com.datn.electronic_voting.exception.ErrorCode;
import com.datn.electronic_voting.repositories.CandidateRepository;
import com.datn.electronic_voting.repositories.ElectionRepository;
import com.datn.electronic_voting.repositories.UserRepository;
import com.datn.electronic_voting.repositories.VoteRepository;
import com.datn.electronic_voting.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IVoteService implements VoteService {

    private final VoteRepository voteRepository;

    private final ElectionRepository electionRepository;

    private final UserRepository userRepository;

    private final CandidateRepository candidateRepository;

    private final String GENERATOR ="03FB32C9B73134D0B2E77506660EDBD484CA7B18F21EF205407F4793A1A0BA12510DBC15077BE463FFF4FED4AAC0BB555BE3A6C1B0C6B47B1BC3773BF7E8C6F62901228F8C28CBB18A55AE31341000A650196F931C77A57F2DDF463E5E9EC144B777DE62AAAB8A8628AC376D282D6ED3864E67982428EBC831D14348F6F2F9193B5045AF2767164E1DFC967C1FB3F2E55A4BD1BFFE83B9C80D052B985D182EA0ADB2A3B7313D3FE14C8484B1E052588B9B7D2BBD2DF016199ECD06E1557CD0915B3353BBB64E0EC377FD028370DF92B52C7891428CDC67EB6184B523D1DB246C32F63078490F00EF8D647D148D47954515E2327CFEF98C582664B4C0F6CC41659";

    private final String PRIME ="087A8E61DB4B6663CFFBBD19C651959998CEEF608660DD0F25D2CEED4435E3B00E00DF8F1D61957D4FAF7DF4561B2AA3016C3D91134096FAA3BF4296D830E9A7C209E0C6497517ABD5A8A9D306BCF67ED91F9E6725B4758C022E0B1EF4275BF7B6C5BFC11D45F9088B941F54EB1E59BB8BC39A0BF12307F5C4FDB70C581B23F76B63ACAE1CAA6B7902D52526735488A0EF13C6D9A51BFA4AB3AD8347796524D8EF6A167B5A41825D967E144E5140564251CCACB83E6B486F6B3CA3F7971506026C0B857F689962856DED4010ABD0BE621C3A3960A54E710C375F26375D7014103A4B54330C198AF126116D2276E11715F693877FAD7EF09CADB094AE91E1A1597";

    private final BigInteger g = new BigInteger(GENERATOR, 16);

    private final BigInteger p = new BigInteger(PRIME,16);

//    static final BigInteger p = BigInteger.valueOf(1019); // số nguyên tố lớn
//    static final BigInteger g = BigInteger.valueOf(2);
    private final BigInteger q = new BigInteger("08CF83642A709A097B447997640129DA299B1A47D1EB3750BA308B0FE64F5FBD3", 16);

    @Override
    public Vote createVote(Vote vote,boolean voteChoice) {
        checkInforVote(vote);
        SecureRandom random = new SecureRandom();
        BigInteger x_i = new BigInteger(q.bitLength(), random).mod(q);


        // Tính gx = g^x_i mod p
        BigInteger g_xi = g.modPow(x_i, p);

        // Lấy danh sách tất cả gx trước đó để tính gy
        List<Vote> allVotes = voteRepository.findAllByElectionIdAndCandidateId(vote.getElectionId(), vote.getCandidateId());
        List<BigInteger> gxList = allVotes.stream()
                .map(v -> new BigInteger(v.getGx()))
                .collect(Collectors.toList());

        gxList.add(g_xi);
        int i = gxList.size(); // chỉ số của vote mới (vote tiếp theo)


        // Tính g^yi
        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;

        for (int j = 0; j < i; j++) {
            numerator = numerator.multiply(gxList.get(j)).mod(p);
        }
        for (int j = i+1 ; j < gxList.size(); j++) {
            denominator = denominator.multiply(gxList.get(j)).mod(p);
        }

        BigInteger g_yi = numerator.multiply(denominator.modInverse(p)).mod(p);


        // Mã hóa encryptedVote
        BigInteger encryptedVote;
        if (voteChoice) { // Nếu chọn đồng ý
            encryptedVote = g_yi.modPow(x_i, p).multiply(g).mod(p);
        } else { // Nếu chọn không đồng ý
            encryptedVote = g_yi.modPow(x_i, p).mod(p);
        }

        // ZKP theo Schnorr
        BigInteger r = new BigInteger(256, random).mod(p);
        BigInteger t = g.modPow(r, p); // t = g^r mod p

        // Hash challenge: H(g, g^x, gy, encryptedVote, t)
        BigInteger c = hashChallenge(g, g_xi, g_yi, encryptedVote, t, p);

        BigInteger s = r.add(c.multiply(x_i)).mod(p.subtract(BigInteger.ONE));

        boolean verifyZKP = verifyZKP(g,g_xi,t,s,p,g_yi,encryptedVote);
        if (!verifyZKP) {
            throw new AppException(ErrorCode.TIME_ERROR);
        }
        // Lưu gx và encryptedVote vào
        vote.setEncryptedVote(encryptedVote.toString());
        vote.setGy(g_yi.modPow(x_i,p).toString());
        vote.setGx(g_xi.toString());
        vote.setZk_s(s);
        vote.setZk_t(t);
        return voteRepository.save(vote);
    }

    @Override
    public int countAgreeVotes(Long electionId, Long candidateId) {
        BigInteger encryptedTotal = BigInteger.ONE;
        BigInteger gyTotal = BigInteger.ONE;

        List<Vote> voteList = voteRepository.findAllByElectionIdAndCandidateId(electionId,candidateId);
        if(voteList.isEmpty()) throw new AppException(ErrorCode.VOTELIST_NULL);

        for (Vote vote : voteList) {
            encryptedTotal = encryptedTotal.multiply(new BigInteger(vote.getEncryptedVote()));
            gyTotal = gyTotal.multiply(new BigInteger(vote.getGy()))
                    .mod(p);
        }

        BigInteger gyInv = gyTotal.modInverse(p);
        BigInteger gk = encryptedTotal.multiply(gyInv).mod(p);
        return shanks(g, gk, p,voteList.size()); // Trả về số phiếu đồng ý
    }

    @Override
    public Vote updateVote(Vote vote,Long id) {
        vote.setId(id);
        Vote rs = voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        vote.setVoteTime(rs.getVoteTime());
        checkInforVote(vote);
        return voteRepository.save(vote);
    }

    @Override
    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    @Override
    public List<Vote> getVotesPageable(Pageable pageable) {
        return voteRepository.findAll(pageable).getContent();
    }

    @Override
    public Vote findVoteById(Long id) {
        return voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
    }

    @Override
    public void deleteVote(Long id) {
        voteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOTE_NOT_FOUND));
        voteRepository.deleteById(id);
    }

    @Override
    public List<Vote> getVoteByElectionAndCandidateId(Long electionId,Long candidateId) {
        return voteRepository.findAllByElectionIdAndCandidateId(electionId,candidateId);
    }

    private void checkInforVote(Vote vote){
        electionRepository.findById(vote.getElectionId()).orElseThrow(() -> new AppException(ErrorCode.ELECTION_NOT_FOUND));
        userRepository.findById(vote.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_IS_NOT_EXISTS));
        candidateRepository.findById(vote.getCandidateId()).orElseThrow(() -> new AppException(ErrorCode.CANDIDATE_NOT_FOUND));
    }

    private BigInteger hashChallenge(BigInteger g, BigInteger gx, BigInteger gy, BigInteger encryptedVote, BigInteger t, BigInteger p) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(g.toByteArray());
            digest.update(gx.toByteArray());
            digest.update(gy.toByteArray());
            digest.update(encryptedVote.toByteArray());
            digest.update(t.toByteArray());
            byte[] hash = digest.digest();
            return new BigInteger(1, hash).mod(p);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);

        }
    }
    public boolean verifyZKP(BigInteger g, BigInteger gx, BigInteger t, BigInteger s, BigInteger p, BigInteger gy, BigInteger encryptedVote) {
        // Tính lại challenge c
        BigInteger c = hashChallenge(g, gx, gy, encryptedVote, t, p);

        // Kiểm tra: g^s ?= t * gx^c mod p
        BigInteger left = g.modPow(s, p);
        BigInteger right = t.multiply(gx.modPow(c, p)).mod(p);

        return left.equals(right);
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
